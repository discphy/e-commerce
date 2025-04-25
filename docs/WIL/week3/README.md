# 항해 플러스 3주차 - 클린 레이어드 아키텍처

## 🙋‍♂️ 클린 레이어드 아키텍처가 뭐죠...?

클린 레이어드 아키텍처(Clean Layered Architecture)는  
**계층형 아키텍처(Layered Architecture)와 클린 아키텍처(Clean Architecture)의 개념을 통합한 구조**이다. 

즉, 계층형 구조를 기반으로 하되,  
**의존성 역전 원칙(DIP: Dependency Inversion Principle)을** 적용하여 **도메인 레이어를 중심**에 두는 구조다.

각 레이어는 고유한 책임을 가지며,  
**도메인 레이어는 어떤 외부 레이어도 알지 않는다는 점이 핵심**이다.

나는 클린 레이어드 아키텍처를 처음 접했기 때문에,  
처음엔 개념을 쉽게 이해하기 어려웠다. 😂

> 아래 이미지는 개념을 스스로 정리하기 위해 직접 그려본 그림이다.

![img.png](img.png)

## 🧱 클린 아키텍처 레이어 구조

### 📌 프레젠테이션 레이어 (Presentation Layer)

**HTTP 요청과 응답을 처리하는 레이어로, 외부 요청을 받아 Application Layer 에 전달하고, 결과를 반환한다.**

+ 역할 : HTTP 요청을 받고 응답 반환
+ 구성 : @RestController, @Controller
+ 책임 : 사용자 입력 검증, API 응답 포맷 변환, 요청 파라미터 매핑 
+ 의존 대상 : Application Layer

#### 패키지 및 클래스 네이밍

| 패키지명         | 클래스             | 요청 DTO       | 응답 DTO        |
|--------------|-----------------|--------------|---------------|
| `interfaces` | `XxxController` | `XxxRequest` | `XxxResponse` |

#### 레이어 패키지 구조 (주문 도메인 예시)

```text
interfaces
└── order
    ├── OrderController.java    # 주문 컨트롤러
    ├── OrderRequest.java       # 요청 DTO
    └── OrderResponse.java      # 응답 DTO
```

#### Controller 클래스 예시

```java
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;  // 주문 파사드 주입

    @PostMapping("/api/v1/orders")
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        OrderResult orderResult = orderFacade.createOrder(OrderCriteria.of(request)); // 주문 파사드 호출
        return ApiResponse.success(OrderResponse.of(orderResult)); // 응답 DTO 변환
    }
}
```

### 📌 애플리케이션 레이어 (Application Layer)

**도메인 서비스들을 조합하여 비지니스 로직을 처리하는 레이어로, 트랜잭션 제어와 흐름 제어에 관여한다.**

+ 역할 : 유즈케이스 실행 및 트랜잭션 관리
+ 구성 : @Service, Facade
+ 책임 : 프레젠테이션 레이어와 도메인 레이어 간 연결, 트랜잭션 단위 관리, 여러 도메인 서비스 호출
+ 의존 대상 : Domain Layer

#### 패키지 및 클래스 네이밍

| 패키지명          | 클래스         | 요청 DTO        | 응답 DTO      |
|---------------|-------------|---------------|-------------|
| `application` | `XxxFacade` | `XxxCriteria` | `XxxResult` |


#### 레이어 패키지 구조 (주문 도메인 예시)

```text
application
└── order
    ├── OrderCriteria.java    # 요청 DTO
    ├── OrderResult.java      # 응답 DTO
    └── OrderFacade.java      # 주문 파사드
```

#### Facade 클래스 예시

```java
@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService; // 주문 도메인 서비스 주입
    private final PaymentService paymentService; // 결제 도메인 서비스 주입

    @Transactional
    public OrderResult createOrder(OrderCriteria criteria) {
        Order order = orderService.createOrder(criteria.toCommand()); // 주문 도메인 서비스 호출
        paymentService.payment(order); // 결제 도메인 서비스 호출
        return OrderResult.of(order); // 응답 DTO 변환
    }
}
```

### 📌 도메인 레이어 (Domain Layer)

**비즈니스 로직을 처리하는 레이어로, 도메인 모델과 도메인 서비스를 포함한다.**

+ 역할 : 비즈니스 로직 처리
+ 구성 : Domain Model, Domain Service, Domain Repository, Enum, VO
+ 책임 : 도메인 모델과 도메인 서비스 구현 / 도메인 객체 간의 협력 및 책임 / 상태 관리
+ 의존 대상 : 없음 ❌

#### 패키지 및 클래스 네이밍

| 패키지명     | 클래스          | 요청 DTO       | 응답 DTO    |
|----------|--------------|--------------|-----------|
| `domain` | `XxxService` | `XxxCommand` | `XxxInfo` |

#### 레이어 패키지 구조 (주문 도메인 예시)

```text
domain
└── order
    ├── Order.java              # 주문 도메인 객체
    ├── OrderCommand.java       # 요청 DTO
    ├── OrderInfo.java          # 응답 DTO
    ├── OrderProduct.java       # 주문 상품 도메인 객체
    ├── OrderRepository.java    # 주문 도메인 레포지토리 인터페이스
    ├── OrderService.java       # 주문 도메인 서비스
    └── OrderStatus.java        # 주문 상태 Enum
```

#### 도메인 서비스 예시

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository; // 주문 도메인 레포지토리 주입

    public Order createOrder(OrderCommand command) {
        Order order = Order.create(command.getProducts()); // 주문 도메인 객체 생성
        return orderRepository.save(order); // 주문 도메인 레포지토리 저장
    }
}
```

### 📌 인프라 레이어 (Infrastructure Layer)

**외부 시스템과의 연동을 처리하는 레이어로, 데이터베이스와의 연동, 외부 API 호출 등을 처리한다.**

+ 역할 : 외부 시스템과의 연결
+ 구성 : @Repository, JPA/MyBatis/QueryDSL, 외부 API 호출, 메시지 큐 등
+ 책임 : DB 연동, 외부 API 호출, 파일, Redis, Kafka
+ 의존 대상 : Domain Layer 인터페이스 구현

#### 레이어 패키지 구조 (주문 도메인 예시)

```text
infrastructure
└── order
    ├── repository
    │   ├── OrderJpaRepository.java         # JPA 레포지토리
    │   ├── OrderQueryDslRepository.java    # QueryDSL 레포지토리
    │   └── OrderRepositoryImpl.java        # 레포지토리 구현체
    ├── client
    │   └── OrderClientImpl.java            # 외부 API 호출
    └── message
        └── OrderMessageSenderImpl.java     # 메시지 큐
```

#### 레포지토리 구현체 예시

```java
@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    
    private final OrderJpaRepository orderJpaRepository; // JPA 레포지토리 주입
    
    public Order save(Order order) {
        return orderJpaRepository.save(order); // JPA 레포지토리 저장
    }
}
```

## ⏮️ 과제 Rollback

**과제 제출을 이틀 앞두고**, 나는 과감하게 **기존 브랜치를 날리고 처음부터 다시 작성**하기로 결심했다.

이유는 이렇다.  
제출을 이틀 앞둔 오전, 클린 아키텍처에 대한 큰 깨달음을 얻게 되었고,  
같은 날 오후 멘토링을 통해 그 확신이 더욱 강해졌기 때문이다.

주어진 시간이 단 이틀뿐이라... 결국 연차를 쓰지 않을 수 없었다.  
(정말로 이틀 만에 끝내야 했던 것이다.)

기존 코드에서 발견한 문제점은 다음과 같다.

```text
- 저수준 모듈이 고수준 모듈을 의존
- 도메인 클래스 간의 강결합
- 도메인 클래스의 관심사 분리 부족
- 도메인 클래스의 책임이 불명확
```

한마디로, **DIP(의존성 역전 원칙)과** **객체지향 설계 원칙** 사이에서 많은 혼란이 있었던 것 같다.

단순한 리팩토링만으로는 구조를 깔끔하게 정리하기 어려웠고,  
고민 끝에 처음부터 다시 작성하는 편이 낫겠다고 판단했다.

그리고 그렇게, 다시 과제를 수행하면서 진지하게 고민했던 부분들이 생겼다.

## 💭 과제 고민했던 부분

### 📌 도메인 간 협력? or 강결합?

다음은 **주문 도메인 클래스**에 **쿠폰**을 적용하는 예시이다.

**인자 값으로 쿠폰 ID를 받는 게 좋을지?**
```java
class Order {

    private Long couponId; 

    private Order(Long couponId) {
        this.couponId = couponId;
    }
}
```

아니면, 인자 값으로 쿠폰 도메인 자체를 받는 것이 맞을지?
```java
class Order {

    private Long couponId; 

    private Order(Coupon coupon) {
        this.couponId = coupon.getId(); 
    }
}
```
코치님들 사이에서도 의견이 분분한 내용이다.  
물론 **은탄환은 없지만**, 객체지향적인 관점에서 보았을 때 아래 방식이 더 적절하다고 생각한다.

`Order`와 `Coupon`의 관심사는 어느 정도 존재한다고 보기 때문에, 이들 간의 협력이 필요하다고 생각한다.  
`couponId`를 그대로 받으면 어떤 `Long` 값이 들어와도 검증되지 않기 때문에, 이 방식은 선호하지 않는다.

반면, 쿠폰 도메인을 받았을 때는 **쿠폰의 메서드**를 사용할 때 **책임을 잘 분리**해서 사용해야 한다.  
예를 들어, 주문 도메인에서 쿠폰 도메인의 **발행 메서드를 사용**하면, **쿠폰 도메인에 대한 의존성**이 생기기 때문에 좋지 않다.

이러한 이유로, **도메인 클래스와 엔티티 클래스는 구분**하는 것이 바람직하다고 본다.  
(하지만, 실제로 현업에서는 도메인 클래스와 엔티티 클래스를 동일시하는 경우도 많다.)

그렇지만, 도메인 클래스가 **ID 식별자만 가지고 있다면**, 객체 지향적인 측면에서보다는 **테이블 지향적인 클래스**가 될 위험이 크다.

### 📌 레이어 간 DTO -> 오버 엔지니어링?

| 패키지명          | 클래스             | 요청 DTO        | 응답 DTO        |
|---------------|-----------------|---------------|---------------|
| `domain`      | `XxxService`    | `XxxCommand`  | `XxxInfo`     |
| `application` | `XxxFacade`     | `XxxCriteria` | `XxxResult`   |
| `interfaces`  | `XxxController` | `XxxRequest`  | `XxxResponse` |

처음엔 **DTO를 레이어별로 구분하는 것이 오버 엔지니어링**처럼 느껴질 수 있다.  
하지만 이 구분은 **각 레이어의 책임을 명확히 하기 위함**이다.

또한 DTO는 **레이어 간 결합도를 낮추는 완충제 역할**을 한다.

예를 들어, 프레젠테이션 레이어에서 도메인 레이어의 DTO를 그대로 사용하게 되면  
API 스펙이 변경되거나 버저닝이 발생할 때, 도메인 레이어의 DTO도 함께 변경되어야 한다.

이는 곧, **도메인 레이어가 프레젠테이션 레이어에 의존**하게 되는 결과로 이어지고,  
레이어 간 결합도가 높아진다.

반면, 프레젠테이션 전용 DTO(`XxxRequest`, `XxxResponse`)를 따로 두면  
API 스펙이 변경되더라도 도메인 레이어는 영향을 받지 않는다.

지금은 다소 과한 설계처럼 보일 수 있지만,  
**유지보수 관점에서 보면 도메인과 프레젠테이션 간 결합도를 낮추는 것이 훨씬 유리**하다.

### 📌 파사드 패턴 꼭 써야 할까?

결론부터 말하자면, **파사드 패턴은 "울며 겨자먹기"로 사용하는 경우가 많다.**

파사드(Facade) 패턴은 **복잡한 서브시스템을 하나의 인터페이스로 감싸, 외부에서 쉽게 사용할 수 있도록 만드는 구조**이다.

도메인 서비스는 **자신의 도메인에 국한된 책임만 가지며**, 다른 도메인의 로직에 직접 관여해서는 안 된다.

하지만 하나의 기능을 수행하기 위해 **여러 도메인 서비스를 조합**해야 하는 경우가 많다.  
이때 도메인 서비스 간 의존성을 줄이기 위해 **중간 조율자 역할로 파사드 패턴**을 사용한다.

반대로, 단일 도메인 서비스만 사용하는 상황이라면 파사드 클래스를 굳이 만들 필요는 없다.  
**오히려 구조가 복잡해지고, 불필요한 파일만 늘어날 수 있다.**

> 참고로, Facade라는 단어는 '건축물의 정면'을 의미한다.  
> 즉, Facade 클래스 하나만 보면 전체 로직의 흐름이 보이도록 구성하는 것이 이상적이다. 

### 📌 코드의 비중 중 검증 로직이 80%?

실제로 코드를 작성해보면,  
전체 코드 중 **검증 로직이 차지하는 비중이 80%에 달하는 경우도 많다.**

그렇다면, 이 검증 로직은 어디에 두는 것이 좋을까?

- **도메인 서비스?**
- **도메인 요청 DTO(Command)?**
- **도메인 객체 자체?**

나는 개인적으로 **도메인 객체 내부에 검증 로직을 두는 것**을 선호한다.

물론, 요청 DTO(Command)에 검증을 두는 것도  
**Early Validation**이라는 점에서 합리적일 수 있다.

하지만 이 방식은 **모든 요청 DTO마다 검증 로직을 반복적으로 구현해야 하며**, 중복과 분산된 검증 책임이라는 단점이 있다.  
또한 DTO는 테스트 커버리지가 약해지는 경향도 있기 때문에, **도메인 객체 쪽에도 동일한 검증을 이중으로 둬야 할 수도 있다.**

### 📌 도메인 클래스와 JPA 엔티티 클래스간의 분리해야 될까?

많은 회사들이 **도메인 클래스와 JPA 엔티티 클래스를 구분하지 않고** 사용하는 것으로 알고 있다.  
하지만 나는 **두 클래스를 분리하는 것이 바람직하다**고 생각한다.

JPA 엔티티와 도메인을 통합할 때 발생하는 문제점은 다음과 같다.

---

#### 1️⃣ JPA에 의존적인 도메인 구조가 된다.  
   
추후 ORM을 제거하거나 변경할 경우, 도메인 클래스를 함께 수정해야 한다.

#### 2️⃣ 객체 간 협력이 ID 기반으로 제한된다.  
   
도메인 간 협력보다 테이블 간 참조에 가까워지며, 연관관계를 맺지 않으면 `@Transient` 등의 우회가 필요하다.

#### 3️⃣ 도메인이 인프라 레이어에 의존하게 된다.  

DIP를 위반하게 되고, 구현체가 도메인에 섞이는 문제가 생긴다.

#### 4️⃣ 도메인 클래스의 관심사가 분리되지 않는다.  
   
로직보다 테이블 구조 중심으로 설계가 흘러가게 된다.

---

물론, 분리를 한다면 **잘 분리된 구조로 설계**해야 한다.  
어설프게 나누면 오히려 설계 복잡도만 증가하고, 독이 될 수 있다.

이번 과제에서는 “**코치님보다 코드를 잘 짜야 도메인과 엔티티를 나눌 수 있다**”는(?) 조건이 있었기 때문에  
**엔티티 클래스를 도메인 클래스로 겸해서 사용**했다.

하지만 이후 리팩토링을 통해  
**진짜 도메인 중심 구조로 개선**해볼 생각이다.

### 📌 JPA 연관관계는 최소화 해야될까?

**JPA 생명주기가 완전히 동일하거나, 루트 애그리거트 간의 관계**라면  
연관관계를 맺는 것이 비교적 안전하다고 생각한다.

하지만 이 외의 경우에는 **연관관계를 신중히 고려해야 한다.**  
무리한 연관관계는 다음과 같은 문제를 유발할 수 있다:

- **예상치 못한 영속성 전이 및 지연 로딩 문제**
- **JPA 사이클과 도메인 로직 간 충돌**
- **결과적으로 도메인 클래스가 JPA에 종속되는 구조**

---

이번 과제를 진행하면서  
이처럼 다양한 관점에서 고민한 내용을 하나하나 정리해보았다.

사실 이 모든 질문에 정답이 있는 것은 아니다.  
**개인의 스타일, 도메인 복잡도, 그리고 팀의 아키텍처 철학에 따라 달라질 수 있는 영역**이다. 🙃

## 👍👍 두번째 BP

많은 우여곡절과 고민 끝에 과제를 마무리하고 제출했다.  
그런데도 아키텍처와 클린 코드에 관심이 많아서, 과정 자체가 정말 재미있었다.

> _잘 다듬어진 코드를 보면 힐링되는 타입이다... 🍀_

🔗 [과제 PR](https://github.com/discphy/hhplus-e-commerce/pull/17)

열심히, 그리고 진심으로 잘해보려 노력한 결과, 1주차에 이어 **"BP👍"를 또 한 번 받게 되었다.** 

![img_1.png](img_1.png)

이번 과제에서 받은 피드백 중 인상 깊었던 것은 다음과 같다.

> _"도메인이 JPA에 의존하는데, `JpaRepository`가 infra 레이어에 위치해 있는 건 구조적으로 일관성이 부족해 보입니다."_

이 말에 깊이 공감했다.  
**도메인 자체가 JPA에 의존한다면**, 해당 구현체를 infra에 위치시키는 것이 어색할 수밖에 없다.

가장 자연스러운 방식은,  
**도메인 클래스는 JPA에 의존하지 않고 독립적으로 존재하며**,  
**infra 레이어에 별도로 엔티티와 Repository 구현이 분리**되어 있는 구조라고 생각한다.

## 🏗️ 클린 아키텍처를 사용해보니...

이번 과제를 통해 처음으로 **클린 아키텍처 구조를 실전에서 적용**해보았다.  
DIP, SRP 등 **SOLID 원칙을 체감하며 구현**할 수 있었고,  
코드의 **안정성과 유지보수성 측면에서 매우 만족스러웠다.**

> 물론 헥사고날 아키텍처도 충분히 훌륭하다고 생각한다.  
> 하지만 러닝커브와 오버 엔지니어링의 리스크가 분명 존재한다.

그래서 사내에서 진행 중인 **신규 자사 플랫폼 개발에도**  
이번 아키텍처와 개발 패턴을 적용해 보기로 했다.  
문서화와 발표를 통해 팀원들을 설득했고, 결국 **공식적으로 도입이 확정되었다. 😎**

---

이번 주차는 정말 많은 것을 배우고, 스스로도 한 단계 성장할 수 있었던 시간이었다.

과제가 거듭될수록 난이도는 점점 올라가 WIL 작성이 늦어지고 있지만  
**최대한 꼼꼼하게 기록해나갈 예정이다.**