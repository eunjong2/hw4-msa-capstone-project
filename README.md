

# Hanwha Delivery system

#### 팀장 : 김은종 대리(디지털워크그룹)<br>
#### 팀원 : 최수빈 사원(빅데이터팀), 윤영진 대리(디지털에셋그룹)
---


## 분석설계
### 서비스 시나리오
1. 고객이 메뉴를 선택하여 주문한다
2. 고객이 결제한다
3. 결제가 완료되면 상점 주문 내역에 저장된다
4. 고객이 주문을 취소할 수 있다.
5. 상점 주인이 주문을 확인하여 요리를 시작한다
6. 요리가 완료되면 배달 리스트에 주문 내역이 저장된다
7. 배달 기사가 요리를 픽업하여 배달을 시작한다.
8. 고객이 배달 완료를 확인한다.
9. 고객이 주문 상태를 중간 중간 조회한다.
10. 주문 상태가 바뀔 때 마다 알림을 보낸다.

### 이벤트 스토밍
- 최종 이벤트 스토밍 결과
<img width="1469" alt="스크린샷 2022-07-26 오전 9 29 46" src="https://user-images.githubusercontent.com/31244884/181143461-14e0b946-f237-49bd-9081-8ddc20b82ef2.png">

- Order
<img width="1469" alt="EventStorming_Order" src="https://user-images.githubusercontent.com/31244884/181143488-4eb3b831-31a6-445f-bfbc-6f28400a462c.png">

```
- 고객이 메뉴를 선택하여 주문한다.
- 고객이 결제한다.
- 주문이 되면 상점에 주문내역이 추가된다.
- 상점 주인이 이를 accept하여 요리를 시작한다.
- 요리가 완료되면 배달 깃사의 배달리스트에 추가된다.
- 배달 기사가 음식 픽업을 하여 배달을 시작한다.
- 배달이 완료되면 고객이 주문을 확인한다.
- 주문 상태가 변경될 때 마다 알림을 보낸다.
```

- Cancel
<img width="1469" alt="EventStorming_Cancel" src="https://user-images.githubusercontent.com/31244884/181143520-c731acc6-0d24-4a2b-9452-2ab4b92098cc.png">

```
- 고객이 주문을 취소할수 있다.
- 주문이 취소되면 결제가 취소되고 상점에도 취소된다.(단, 취소는 상태가 "Ordered"의 경우에만 가능하다.)
- 상태(Ordered, Accpeted, Finished)
```

- 트랜잭션 처리
```
- 고객 주문시 결제 처리
	- 고객 주문시 결제가 완료되어야 주문을 받을수 있음 > Request-Response 방식 처리
- 고객 주문 최소시 결제 취소 처리
	- 고객 주문 취소시 
- 결제 완료시 상점 주문 리스트 추가, 요리 완료시 배달 리스트 추가
	- Eventual Consistency 방식으로 트랜잭션 처리함.
- 주문, 배달 상태 알림
	- Eventual Consistency 를 기본으로 채택함.
```

## SAGA
+ 구현<p>
    서비스를 Local에서 아래와 같은 방법으로 서비스별로 개별적으로 실행한다.
   
```
    cd Order
    mvn spring-boot:run
```
```
    cd Payment
    mvn spring-boot:run 
```
```
    cd Store
    mvn spring-boot:run  
```
```
    cd Delivery
    mvn spring-boot:run  
```
```
    cd management
    mvn spring-boot:run
```

+ DDD적용<p>
    6개의 도메인으로 관리되고 있으며 `주문(Order)`, `결제(Payment)`, `상점(Store)`, `배송(Delivery)`, `관리(management)`, `프론트엔드로(frontend)` 구성된다.
 
```diff
    	
@Entity
@Table(name = "Order_table")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String menuName;
    private Long orderId;
    private String address;
    private Integer qty;
    private String orderStatus;
    private Double price;

+    @PostPersist
    public void onPostPersist() {
        // MenuCancelled menuCancelled = new MenuCancelled(this);
        // menuCancelled.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.
        MenuOrdered menuOrdered = new MenuOrdered(this);
        menuOrdered.publishAfterCommit();
        
+        hanwhadeliverysystemteam.external.Payment payment = new hanwhadeliverysystemteam.external.Payment();
        payment.setOrderId(menuOrdered.getOrderId());
        payment.setPaymentStatus("Ordered");
        payment.setPrice(menuOrdered.getPrice());
        payment.setQty(menuOrdered.getQty());
        payment.setMenuName(menuOrdered.getMenuName());
        payment.setAddress(menuOrdered.getAddress());

        // mappings goes here
        OrderApplication.applicationContext
            .getBean(hanwhadeliverysystemteam.external.PaymentService.class)
            .pay(payment);

        // MenuOrdered menuOrdered = new MenuOrdered(this);
        // menuOrdered.publishAfterCommit();
    }

    public static OrderRepository repository() {
        OrderRepository orderRepository = OrderApplication.applicationContext.getBean(
            OrderRepository.class
        );
        return orderRepository;
    }
    public void cancel() {
        MenuCancelled menuCancelled = new MenuCancelled(this);
        // menuCancelled      
        menuCancelled.publishAfterCommit();
    }
}
```
   
+ 서비스 호출흐름(Sync)<p>
`주문(Order)` -> `결제(Payment)`간 호출은 동기식으로 일관성을 유지하는 트랜젝션으로 처리
* 고객이 메뉴를 선택하고 주문을 요청한다.
* 결제서비스를 호출하기위해 FeinClient를 이용하여 인터페이스(Proxy)를 구현한다.
* 주문을 받은 직후(`@PostPersist`) 결제를 요청하도록 처리한다.


## CQRS Pattern
* `주문(Order)`, `결제(Payment)` 서비스 실행

```
    cd Order
    mvn spring-boot:run
```

```
    cd Payment
    mvn spring-boot:run 
```

* 주문 요청, 취소 요청
```
- http localhost:8081/orders address="suwon" menuName="pizza" orderId=1 orderStatus="Ordered" price=1000 qty=10

- http PUT localhost:8081/orders/1/cancel
```
```
HTTP/1.1 201 

{
    "_links": {
        "order": {
            "href": "http://localhost:8081/orders/1"
        },
        "self": {
            "href": "http://localhost:8081/orders/1"
        }
    },
    "address": "suwon",
    "menuName": "pizza",
    "orderId": 1,
    "orderStatus": "Ordered",
    "price": 1000.0,
    "qty": 10
}

{
    "address": "suwon",
    "id": 1,
    "menuName": "pizza",
    "orderId": 1,
    "orderStatus": "Cancelled",
    "price": 1000.0,
    "qty": 10
}
```

* 카프카 모니터링
```
/usr/local/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic hanwhaDeliverySystem --from-beginning
```
```
{"eventType":"MenuOrdered","timestamp":1658881581353,"id":2,"menuName":"pizza","orderId":1,"address":"suwon","qty":10,"orderStatus":"Ordered","price":1000.0}
{"eventType":"MenuCancelled","timestamp":1658882293371,"id":1,"menuName":"pizza","orderId":1,"address":"suwon","qty":10,"orderStatus":"Ordered","price":1000.0}
```
* management 실행

```
cd management
mvn spring-boot:run
```
* management Query Model을 통해 상태를 `통합조회` 가능하다


```
{
    "_embedded": {
        "statusChecks": [
            {
                "_links": {
                    "self": {
                        "href": "http://localhost:8083/statusChecks/1"
                    },
                    "statusCheck": {
                        "href": "http://localhost:8083/statusChecks/1"
                    }
                },
                "orderId": 1,
                "status": "Cancelled"
            }
        ]
    },
    "_links": {
        "profile": {
            "href": "http://localhost:8083/profile/statusChecks"
        },
        "search": {
            "href": "http://localhost:8083/statusChecks/search"
        },
        "self": {
            "href": "http://localhost:8083/statusChecks"
        }
    },
    "page": {
        "number": 0,
        "size": 20,
        "totalElements": 1,
        "totalPages": 1
    }
}
```

## Correlation / Compensation(Unique Key)

* 주문 성공 후, 주문 취소 시 서비스 간 카프카 이벤트 수신을 통해 주문 취소 구현
* 주문 취소 -> 결제 취소 (단, 주문 상태 확인 필요)

```
    public void wheneverMenuCancelled_CancelPayment(
        @Payload MenuCancelled menuCancelled
    ) {
        MenuCancelled event = menuCancelled;
        System.out.println(
            "\n\n##### listener CancelPayment : " + menuCancelled + "\n\n"
        );
        String orderStatus = event.getOrderStatus();
        if(!"Accepted".equals(orderStatus) || "Finished".equals(orderStatus)){
            Payment.cancelPayment(event);
        }
    }

```

## Req / Resp (feign client)

* Interface 선언을 통해 자동으로 Http Client 생성
* Annotation만으로 Http Client를 만들수 있고, 이를 통해서 원격의 Http API호출이 가능<br>

* Dependency 추가
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```
* FeignClient Interface
```
@FeignClient(name = "Payment", url = "${api.url.Payment}")
public interface PaymentService {
    @RequestMapping(method = RequestMethod.POST, path = "/payments")
    public void pay(@RequestBody Payment payment);
    // keep

}
```

## Gateway
- Gateway를 통해 Endpoint의 요청을 받고 API Service에게 라우팅해준다.<br>
라우팅 정보는 application.yaml에서 확인한다.
```diff
spring:
  profiles: docker
  cloud:
    gateway:
+      routes:
        - id: Order
          uri: http://order:8080
          predicates:
            - Path=/orders/** /menus/**
        - id: Delivery
          uri: http://delivery:8080
          predicates:
            - Path=/deliveries/** 
        - id: management
          uri: http://management:8080
          predicates:
            - Path= /statusChecks/**
        - id: Payment
          uri: http://payment:8080
          predicates:
            - Path=/payments/** 
        - id: Store
          uri: http://store:8080
          predicates:
            - Path=/menus/** 
        - id: frontend
          uri: http://frontend:8080
          predicates:
            - Path=/**
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true

+server:
  port: 8080

```

## Deploy
- deploy 테스트를 위한 k8s pod 생성 
> kind Type 확인 : Deployment, Service <br>
Service Type 확인 : LoadBalancer

```diff
apiVersion: apps/v1
+ kind: Deployment
metadata:
  name: gateway
  labels:
    app: gateway
spec:
  replicas: 1
  selector:
    matchLabels:
      app: gateway
  template:
    metadata:
      labels:
        app: gateway
    spec:
      containers:
        - name: gateway
          image: eunjong4421/gateway:v2
          ports:
            - containerPort: 8080
---
apiVersion: v1
+kind: Service
metadata:
  name: gateway
  labels:
    app: gateway
spec:
  ports:
    - port: 8080
      targetPort: 8080
  selector:
    app: gateway
+  type: LoadBalancer

```
* deploy 생성
```diff
kubectl apply -f deployment.yaml
```
## Circuit Breaker
* DestinationRule 생성
```diff
kubectl apply -f - << EOF
+  apiVersion: networking.istio.io/v1alpha3
  kind: DestinationRule
  metadata:
    name: delivery
  spec:
    host: delivery
-    trafficPolicy:
-      outlierDetection:
-        consecutive5xxErrors: 1
-        interval: 1s
-        baseEjectionTime: 3m
-        maxEjectionPercent: 100
EOF
```

+ Circuit Breaker 테스트 환경설정(`replicas=3`)

```
kubectl scale deploy delivery --replicas=3
```
+ 새 터미널에서 Http Client 컨테이너를 설치하고, 접속한다.
```
kubectl create deploy siege --image=ghcr.io/acmexii/siege-nginx:latest
kubectl exec -it pod/siege-75d5587bf6-29djk -- /bin/bash
```
+ Circuit Breaker 동작 확인
```diff
+root@siege-75d5587bf6-29djk :/# http http://delivery:8080/actuator/echo
- HTTP/1.1 200 OK
root@siege-75d5587bf6-29djk:/# http http://delivery:8080/actuator/echo
HTTP/1.1 200 
Content-Length: 40
Content-Type: text/plain;charset=UTF-8
Date: Tue, 26 Jul 2022 23:43:13 GMT

delivery-67ff6476bb-b26jk/192.168.45.193

root@siege-75d5587bf6-29djk:/# http http://delivery:8080/actuator/echo
HTTP/1.1 200 
Content-Length: 38
Content-Type: text/plain;charset=UTF-8
Date: Tue, 26 Jul 2022 23:43:19 GMT

delivery-67ff6476bb-skcsw/192.168.78.4

root@siege-75d5587bf6-29djk:/# http http://delivery:8080/actuator/echo
HTTP/1.1 200 
Content-Length: 39
Content-Type: text/plain;charset=UTF-8
Date: Tue, 26 Jul 2022 23:43:22 GMT

delivery-67ff6476bb-w5gpw/192.168.22.82

```
+ 새로운 터미널에서 마지막에 출력된 delivery 컨테이너로 접속하여 명시적으로 5xx 오류를 발생 시킨다.

```diff
# 새로운 터미널 Open
# 3개 중 하나의 컨테이너에 접속
kubectl exec -it pod/delivery-67ff6476bb-w5gpw -c delivery -- /bin/sh
#
# httpie 설치 및 서비스 명시적 다운
apk update
apk add httpie
- http PUT http://localhost:8080/actuator/down
```
+ Siege로 접속한 이전 터미널에서 delivery 서비스로 접속해 3회 이상 호출해 본다.
```
http GET http://delivery:8080/actuator/health
```
+ 아래 URL을 통해 3개 중 `2개`의 컨테이너만 서비스 됨을 확인한다.

```diff


root@siege-75d5587bf6-29djk:/# http http://delivery:8080/actuator/echo
HTTP/1.1 200 
Content-Length: 39
Content-Type: text/plain;charset=UTF-8
Date: Tue, 26 Jul 2022 23:43:22 GMT

-delivery-67ff6476bb-w5gpw/192.168.22.82

root@siege-75d5587bf6-29djk:/# http http://delivery:8080/actuator/echo
HTTP/1.1 200 
Content-Length: 39
Content-Type: text/plain;charset=UTF-8
Date: Tue, 26 Jul 2022 23:45:47 GMT

+delivery-67ff6476bb-w5gpw/192.168.22.82


root@siege-75d5587bf6-29djk:/# http http://delivery:8080/actuator/echo
HTTP/1.1 200 
Content-Length: 38
Content-Type: text/plain;charset=UTF-8
Date: Tue, 26 Jul 2022 23:45:52 GMT

-delivery-67ff6476bb-skcsw/192.168.78.4

root@siege-75d5587bf6-29djk:/# http http://delivery:8080/actuator/echo
HTTP/1.1 200 
Content-Length: 38
Content-Type: text/plain;charset=UTF-8
Date: Tue, 26 Jul 2022 23:45:55 GMT

+delivery-67ff6476bb-skcsw/192.168.78.4
```	


## Autoscale(HPA)
- metric server 설치
```sh
# metric server 설치
$ kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/download/v0.3.7/components.yaml

# 설치 확인
$ kubectl get deployment metrics-server -n kube-system
$ kubectl top pods -n kube-system    
```

- auto scaler 설정 및 설정값 확인
```sh
# auto scaler 설정
$ kubectl autoscale deployment order --cpu-percent=20 --min=1 --max=3

# 설정값 확인
$ kubectl get hpa
NAME    REFERENCE          TARGETS    MINPODS   MAXPODS   REPLICAS   AGE
order   Deployment/order   <unknown>/20%   1         3         0          24s
```

- deployment.yaml 파일 수정
```diff
...
    spec:
      containers:
        - name: order
          image: sbchoi29/order:memleak #eunjong4421/order:v2
          ports:
            - containerPort: 8080
+          resources:
+            limits:
+              cpu: 500m
+            requests:
+              cpu: 200m
          readinessProbe:
...
```

- 배포
```sh
# 배포
$ kubectl apply -f Order/kubernetes/deployment.yam
```

- auto scale 설정 테스트
	- seige 명령으로 부하를 주어 pod 개수가 늘어나는 것을 확인
```sh
$ kubectl exec -it siege -- /bin/bash
$ siege -c20 -t40S -v http://order:8080/orders
...
Lifting the server siege...
Transactions:                   2578 hits
Availability:                 100.00 %
Elapsed time:                  39.22 secs
Data transferred:               0.74 MB
Response time:                  0.30 secs
Transaction rate:              65.73 trans/sec
Throughput:                     0.02 MB/sec
Concurrency:                   19.92
Successful transactions:        2578
Failed transactions:               0
Longest transaction:            1.00
Shortest transaction:           0.00
```

- 아래와 같이 order pod 개수가 3개로 늘어난 것을 확인
```diff
$ kubectl get pod
  NAME                       READY   STATUS    RESTARTS   AGE
  my-kafka-0                 1/1     Running   2          18h
  my-kafka-1                 1/1     Running   1          18h
  my-kafka-2                 1/1     Running   1          18h
  my-kafka-zookeeper-0       1/1     Running   0          18h
  mysql                      1/1     Running   0          33m
  order-5788449577-2dz96     1/1     Running   0          3m7s
+ order-5788449577-j72cs     0/1     Running   0          30s
+ order-5788449577-lz8c4     0/1     Running   0          30s
  payment-58cb577dc9-t9h8v   1/1     Running   0          18h
  siege                      1/1     Running   0          18h
```

- cpu값 늘어난 것을 확인 가능
```diff
$ kubectl get hpa
  NAME    REFERENCE          TARGETS    MINPODS   MAXPODS   REPLICAS   AGE
+ order   Deployment/order   122%/20%   1         3         3          6m54s
```
	
	
	
## Self-Healing(Liveness Probe)
- 메모리 부하 테스트를 위한 코드 수정
```java
# OrderController.java
import java.lang.reflect.Field;
import sun.misc.Unsafe;

@RestController
// @RequestMapping(value="/orders")
@Transactional
public class OrderController {
	...
    @GetMapping("/callMemleak")
    public void callMemleak() {
        try{
            this.memLeak();
        }catch (Exception e)
            e.printStackTrace();
        }
    }

    public void memLeak() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException 
        Class unsafeClass = Class.forName("sun.misc.Unsafe");
        Field f = unsafeClass.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        System.out.print("4..3..2..1...")
        try
        {
            for(;;)
            unsafe.allocateMemory(1024*1024);
        } catch(Error e) {
            System.out.println("Boom :)");
            e.printStackTrace();
        }
    }
}
```

- 패키지 생성 및 도커 push
```sh
# 패키지 생성
$ mvn package -B

# docker push
$ docker build -t sbchoi29/order:memleak .
$ docker push sbchoi29/order:memleak
```

- deployment.yaml 수정
```diff
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order
  labels:
    app: order
spec:
  replicas: 1
  selector:
    matchLabels:
      app: order
  template:
    metadata:
      labels:
        app: order
    spec:
      containers:
        - name: order
+          image: sbchoi29/order:memleak #eunjong4421/order:v2
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: '/orders'
              port: 8080
            initialDelaySeconds: 10
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 10
+          livenessProbe:
+            httpGet:
+              path: '/actuator/health'
+              port: 8080
+            initialDelaySeconds: 120
+            timeoutSeconds: 2
+            periodSeconds: 5
+            failureThreshold: 5
```

- deployment 배포
```sh
$ kubectl apply -f Order/kubernetes/deployment.yaml
```

- livenessProbe 작동 여부를 확인하기 위해 watch명령어로 pod 상태를 확인한다.
```sh
$ watch kubectl get pod -w
```

- siege에서 메모리 부하 명령어를 수행
```sh
$ kubectl exec -it siege -- /bin/bash

# http http://order:8080/orders
# http http://order:8080/callMemleak
```

- Restarts 카운트가 0에서 1로 증가하는 것을 확인
```diff
  NAME                       READY   STATUS    RESTARTS   AGE
+ order-56769458b8-8mk2f     1/1     Running   1          15m
  payment-58cb577dc9-t9h8v   1/1     Running   0          17h
  siege                      1/1     Running   0          16h
```


## Zero-Downtime Deploy(Readiness Probe)
- 부하 테스트를 위한 siege 생성
```sh
$ kubectl apply -f - <<EOF
> apiVersion: v1
> kind: Pod
> metadata:
>   name: siege
> spec:
>   containers:
>   - name: siege
>     image: apexacme/siege-nginx
> EOF
pod/siege created
```

- siege 작동 확인
```sh
# siege 실행
$ kubectl exec -it siege -- /bin/bas

# siege 정상 작동 확인
root@siege:/# siege -c1 -t2S -v http://order:8080/orders
** SIEGE 4.0.4
** Preparing 1 concurrent users for battle.
The server is now under siege...
HTTP/1.1 200     0.84 secs:     301 bytes ==> GET  /orders
HTTP/1.1 200     0.02 secs:     301 bytes ==> GET  /orders
HTTP/1.1 200     0.02 secs:     301 bytes ==> GET  /orders
HTTP/1.1 200     0.02 secs:     301 bytes ==> GET  /orders
HTTP/1.1 200     0.02 secs:     301 bytes ==> GET  /orders
HTTP/1.1 200     0.02 secs:     301 bytes ==> GET  /orders
HTTP/1.1 200     0.02 secs:     301 bytes ==> GET  /orders
HTTP/1.1 200     0.02 secs:     301 bytes ==> GET  /orders
HTTP/1.1 200     0.02 secs:     301 bytes ==> GET  /orders
HTTP/1.1 200     0.02 secs:     301 bytes ==> GET  /orders
HTTP/1.1 200     0.02 secs:     301 bytes ==> GET  /orders

Lifting the server siege...
Transactions:                     11 hits
Availability:                 100.00 %
Elapsed time:                   1.04 secs
Data transferred:               0.00 MB
Response time:                  0.09 secs
Transaction rate:              10.58 trans/sec
Throughput:                     0.00 MB/sec
Concurrency:                    1.00
Successful transactions:          11
Failed transactions:               0
Longest transaction:            0.84
Shortest transaction:           0.02
```

- readinessProbe 추가 전 부하 테스트 진행
	- siege 내부에서 먼저 부하 수행 명령어를 수행한 후 
	- 다른 터미널에서 배포를 진행하여 부하를 확인한다.
```sh
# 부하 테스트
root@siege:/# siege -c1 -t60S -v http://order:8080/orders --delay=1S

# 배포한다
$ kubectl apply -f deployment.yml
```

- 일부만 성공(74.77 %)하고 나머지는 배포시 중단 된 것을 확인할 수 있음
```diff
** SIEGE 4.0.4
** Preparing 1 concurrent users for battle.
The server is now under siege...
HTTP/1.1 200     0.02 secs:     301 bytes ==> GET  /orders
HTTP/1.1 200     0.01 secs:     301 bytes ==> GET  /orders
HTTP/1.1 200     0.02 secs:     301 bytes ==> GET  /orders
HTTP/1.1 200     0.01 secs:     301 bytes ==> GET  /orders
HTTP/1.1 200     0.01 secs:     301 bytes ==> GET  /orders
...
- [error] socket: unable to connect sock.c:249: Connection refused
- [error] socket: unable to connect sock.c:249: Connection refused

Lifting the server siege...
Transactions:                     80 hits
- Availability:                  74.77 %
Elapsed time:                  59.40 secs
Data transferred:               0.02 MB
Response time:                  0.02 secs
Transaction rate:               1.35 trans/sec
Throughput:                     0.00 MB/sec
Concurrency:                    0.02
Successful transactions:          80
Failed transactions:              27
Longest transaction:            0.05
Shortest transaction:           0.00
```

- Readiness Probe 설정
```diff
# Order > kubernetes > deployment.yaml
apiVersion: apps/v
kind: Deployment
metadata:
  name: order
  labels:
    app: order
spec:
  replicas: 1
  selector:
    matchLabels
      app: order
  template:
    metadata:
      labels:
        app: order
    spec:
      containers:
        - name: order
          image: eunjong4421/order:v2
          ports:
            - containerPort: 8080
 +         readinessProbe:
 +           httpGet:
 +             path: '/orders'
 +             port: 8080
 +           initialDelaySeconds: 10
 +           timeoutSeconds: 2
 +           periodSeconds: 5
 +           failureThreshold: 10
```

- siege를 통한 무중단 배포 테스트
	-  siege 내부에서 먼저 부하 수행 명령어를 수행한 후 
	- 다른 터미널에서 배포를 진행하여 부하를 확인한다.
```sh
# 테스트 수행
root@siege:/# siege -c1 -t60S -v http://order:8080/orders --delay=1S

# 배포한다
$ kubectl apply -f deployment.yml
```

- readinessProbe 설정을 통해 100.00%에 달하는 Availability를 보여주는 것을 확인 가능
```diff
#결과
** SIEGE 4.0.4
** Preparing 1 concurrent users for battle.
The server is now under siege...
HTTP/1.1 200     0.80 secs:     344 bytes ==> GET  /orders
HTTP/1.1 200     0.03 secs:     344 bytes ==> GET  /orders
...
HTTP/1.1 200     0.02 secs:     301 bytes ==> GET  /orders
HTTP/1.1 200     0.02 secs:     301 bytes ==> GET  /orders

Lifting the server siege...
Transactions:                     90 hits
+ Availability:                 100.00 %
Elapsed time:                  59.94 secs
Data transferred:               0.03 MB
Response time:                  0.03 secs
Transaction rate:               1.50 trans/sec
Throughput:                     0.00 MB/sec
Concurrency:                    0.04
Successful transactions:          90
Failed transactions:               0
Longest transaction:            0.80
Shortest transaction:           0.01
```

## Config Map / Persistence Volume / Polyglot
-   PVC 생성
```sh
kubectl apply -f - << EOF
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: fs
  labels:
    app: test-pvc
spec:
  accessModes:
  - ReadWriteOnce
  resources:
    requests:
      storage: 1Mi
EOF
```

-   Secret 객체 생성
```sh
kubectl apply -f - << EOF
apiVersion: v1
kind: Secret
metadata:
  name: mysql-pass
type: Opaque
data:
  password: YWRtaW4=  
EOF
```

-   해당 Secret을 Order Deployment에 설정
```yaml
          env:
            - name: superuser.userId
              value: userId
            - name: _DATASOURCE_ADDRESS
              value: mysql
            - name: _DATASOURCE_TABLESPACE
              value: orderdb
            - name: _DATASOURCE_USERNAME
              value: root
            - name: _DATASOURCE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: mysql-pass
                  key: password
```

-   MySQL 설치
```sh
$ kubectl apply -f - << EOF
apiVersion: v1
kind: Pod
metadata:
  name: mysql
  labels:
    name: lbl-k8s-mysql
spec:
  containers:
  - name: mysql
    image: mysql:latest
    env:
    - name: MYSQL_ROOT_PASSWORD
      valueFrom:
        secretKeyRef:
          name: mysql-pass
          key: password
    ports:
    - name: mysql
      containerPort: 3306
      protocol: TCP
    volumeMounts:
    - name: k8s-mysql-storage
      mountPath: /var/lib/mysql
  volumes:
  - name: k8s-mysql-storage
    persistentVolumeClaim:
      claimName: "fs"
EOF

$ kubectl expose pod mysql --port=3306
```


-   Pod 에 접속하여 hanwhaDeliverySystem 데이터베이스 공간을 만들어주고 데이터베이스가 잘 동작하는지 확인
```diff
# mysql Pod 접속
$ kubectl exec mysql -it -- bash
bash-4.4# echo $MYSQL_ROOT_PASSWORD
admin
bash-4.4# mysql --user=root --password=$MYSQL_ROOT_PASSWORD

# mysql에 orderdb 데이터베이스 생성
+ mysql> create database orderdb;
Query OK, 1 row affected (0.01 sec)

# 데이터 베이스 조회
mysql> show databases
    -> ;
  +--------------------+
  | Database           |
  +--------------------+
  | information_schema |
  | mysql              |
- | orderdb            |
  | performance_schema |
  | sys                |
  +--------------------+
5 rows in set (0.00 sec)

mysql> exit
Bye
```

-   Pod 삭제 후 재생성하고 다시 db에 접속하여  `영속성` 확인
```sh
# mysql pod 삭제
- $ kubectl delete pod/mysql

$ kubectl apply -f - << EOF
apiVersion: v1
kind: Pod
metadata:
  name: mysql
  labels:
    name: lbl-k8s-mysql
spec:
  containers:
  - name: mysql
    image: mysql:latest
    env:
    - name: MYSQL_ROOT_PASSWORD
      valueFrom:
        secretKeyRef:
          name: mysql-pass
          key: password
    ports:
    - name: mysql
      containerPort: 3306
      protocol: TCP
    volumeMounts:
    - name: k8s-mysql-storage
      mountPath: /var/lib/mysql
  volumes:
  - name: k8s-mysql-storage
    persistentVolumeClaim:
      claimName: "fs"
EOF

# mysql pod 접속
$ kubectl exec mysql -it -- bash

# database 접속
bash-4.4# mysql --user=root --password=$MYSQL_ROOT_PASSWORD
```

- 이전에 생성했던 orderdb가 존재하는 것을 확인
```diff
mysql> show databases;
  +--------------------+
  | Database           |
  +--------------------+
  | information_schema |
  | mysql              |
- | orderdb            |
  | performance_schema |
  | sys                |
  +--------------------+
5 rows in set (0.00 sec)

mysql> exit
```
* polyglot의 경우 DB를 mysql 외 다른 DB 적용 혹은 서비스 모듈을 다른 언어로 변경해 배포하여 테스트 가능하다.(추후 적용 예정)

