

# 주문 배달 서비스

## 분석설계
### 서비스 시나리오
1.  고객이 메뉴를 선택하여 주문한다
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

- Order
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
```
- 고객이 주문을 취소할수 있다.
- 주문이 취소되면 결제가 취소되고 상점에도 취소된다?
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

# SAGA
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
    5개의 도메인으로 관리되고 있으며 `주문(Order)`, `결제(Payment)`, `상점(Store)`, `배송(Delivery)`, `관리(management)`로 구성된다.
 
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
```
// PaymentService.java

package hanwhadeliverysystemteam.external;

import ...

@FeignClient(name = "Payment", url = "${api.url.Payment}")
public interface PaymentService {
    @RequestMapping(method = RequestMethod.POST, path = "/payments")
    public void pay(@RequestBody Payment payment);
    // keep

} 
```

- CQRS Pattern

- Correlation / Compensation(Unique Key)

- Request / Response (Feign Client / Sync.Async)

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

+ h-taxi-grap-67ff6476bb-6rzwc/192.168.82.161

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


- Autoscale(HPA)

- Self-Healing(Liveness Probe)

- Zero-Downtime Deploy(Readiness Probe)

- Config Map / Persistence Volume

- Polyglot
