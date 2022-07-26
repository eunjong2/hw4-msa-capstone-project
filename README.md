

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

- SAGA Pattern

- CQRS Pattern

- Correlation / Compensation(Unique Key)

- Request / Response (Feign Client / Sync.Async)

- Gateway

- Deploy / Pipeline

- Circuit Breaker

- Autoscale(HPA)

- Self-Healing(Liveness Probe)

- Zero-Downtime Deploy(Readiness Probe)

- Config Map / Persistence Volume

- Polyglot
