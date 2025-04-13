# 항해 플러스 3주차 - 클린 레이어드 아키텍처

## 🙋‍♂️ 클린 레이어드 아키텍처가 뭐죠...?

클린 레이어드 아키텍처(Clean Layered Architecture)는 
계층형 아키텍처(Layered Architecture)와 클린 아키텍처(Clean Architecture)의 개념을 통합한 구조이다. 

즉, 계층형 구조에서 의존성 역전(DIP, Dependency Inversion Principle)을 통해 도메인을 가장 중심에 두는 것이다.  
각 레이어들의 책임이 존재하며, **도메인 레이어**는 어떤 레이어도 모르는 것이 핵심이다.

나는 클린 레이어드 아키텍처를 처음 들어 봤기 때문에, 이해하기 쉽지 않았다.

__내가 과제 때, 클린 레이어드 아키텍처를 이해하기 위해 그린 그림이다.__
![img.png](img.png)

## 🧱 레이어 별 책임 

### 📌 프레젠테이션 레이어 (Presentation Layer)

**HTTP 요청과 응답을 처리하는 레이어로, 외부 요청을 받아 Application Layer 에 전달하고, 결과를 반환한다.**

그래도 프레젠테이션 레이어는, `interfaces` 라는 패키지 명을 제외하고는 이해하는데에 큰 어려움은 없었다. (주로 `api`라는 패키지명으로 사용했었다.) 
주요 포인트는 응답 DTO를 구성할 때, 도메인을 직접 받아 구성하는 것이 아닌 어플리케이션 레이어의 결과 DTO를 받아 구성하는 것이 핵심이였다.
(프레젠테이션 레이어는 도메인을 몰라야 한다.)

#### 작성 패키지 및 클래스

| 패키지명         | 클래스             | 요청 DTO       | 응답 DTO        |
|--------------|-----------------|--------------|---------------|
| `interfaces` | `XxxController` | `XxxRequest` | `XxxResponse` |

#### 도메인 별 레이어 패키지 구조

```bash
interfaces
├── ApiControllerAdvice.java
├── ApiResponse.java
├── balance
│   ├── BalanceController.java
│   ├── BalanceRequest.java
│   └── BalanceResponse.java
├── order
│   ├── OrderController.java
│   └── OrderRequest.java
├── product
│   ├── ProductController.java
│   └── ProductResponse.java
└── user
    ├── UserCouponController.java
    ├── UserCouponRequest.java
    └── UserCouponResponse.java
```

### 📌 애플리케이션 레이어 (Application Layer)

**도메인 서비스들을 조합하여 비지니스 로직을 처리하는 레이어로, 트랜잭션 제어와 흐름 제어에 관여한다.**

이번 과제를 하면서 파사드 패턴을 처음 사용해보았다.
파사드 패턴이란? 복잡한 서브시스템을 하나로 감싸서 외부에서 쉽게 사용할 수 있도록 만드는 패턴이다.
도메인 서비스는 하나의 도메인에 대한 비즈니스 로직을 처리하는 서비스이므로, 다른 도메인에 관여를 할 수 없기 때문에 파사드를 사용하는 것이다.

하나의 도메인 서비스만 사용할 경우, 파사드 패턴을 사용하지 않아도 된다. 오히려 불필요한 파일들이 생겨 복잡도가 증가할 수 있다.
또한, 요청 DTO 의 네이밍을 `Criteria` 로 사용하였는데 조금 모호한 것 같다. (~~Criteria 는 조회 시 사용하는 네이밍 같다.~~) 

#### 작성 패키지 및 클래스

| 패키지명          | 클래스         | 요청 DTO        | 응답 DTO      |
|---------------|-------------|---------------|-------------|
| `application` | `XxxFacade` | `XxxCriteria` | `XxxResult` |


#### 도메인 별 레이어 패키지 구조

```bash
application
├── balance
│   ├── BalanceCriteria.java
│   ├── BalanceFacade.java
│   └── BalanceResult.java
├── order
│   ├── OrderCriteria.java
│   └── OrderFacade.java
├── product
│   ├── ProductFacade.java
│   └── ProductResult.java
└── user
    ├── UserCouponCriteria.java
    ├── UserCouponFacade.java
    └── UserCouponResult.java
```

### 📌 도메인 레이어 (Domain Layer)

**비즈니스 로직을 처리하는 레이어로, 도메인 모델과 도메인 서비스를 포함한다.**

해당 레이어는 다른 레이어를 알아서는 안된다. 그래서 이 부분이 너무 어려웠다. 
JPA 연관관계는 어디까지 허용을 해야하는지.. 도메인 인스턴스에서 인자 값을 식별자로 받아야 하는지 도메인을 직접 받아야 하는지.. 

#### 작성 패키지 및 클래스

| 패키지명     | 클래스          | 요청 DTO       | 응답 DTO    |
|----------|--------------|--------------|-----------|
| `domain` | `XxxService` | `XxxCommand` | `XxxInfo` |

#### 도메인 별 레이어 패키지 구조

```bash
domain
├── balance
│   ├── Balance.java
│   ├── BalanceCommand.java
│   ├── BalanceInfo.java
│   ├── BalanceRepository.java
│   ├── BalanceService.java
│   ├── BalanceTransaction.java
│   └── BalanceTransactionType.java
├── coupon
│   ├── Coupon.java
│   ├── CouponInfo.java
│   ├── CouponRepository.java
│   ├── CouponService.java
│   └── CouponStatus.java
├── order
│   ├── Order.java
│   ├── OrderCommand.java
│   ├── OrderInfo.java
│   ├── OrderProduct.java
│   ├── OrderRepository.java
│   ├── OrderService.java
│   └── OrderStatus.java
├── payment
│   ├── Payment.java
│   ├── PaymentCommand.java
│   ├── PaymentInfo.java
│   ├── PaymentMethod.java
│   ├── PaymentRepository.java
│   ├── PaymentService.java
│   └── PaymentStatus.java
├── product
│   ├── Product.java
│   ├── ProductCommand.java
│   ├── ProductInfo.java
│   ├── ProductRepository.java
│   ├── ProductSellingStatus.java
│   └── ProductService.java
├── stock
│   ├── Stock.java
│   ├── StockCommand.java
│   ├── StockInfo.java
│   ├── StockRepository.java
│   └── StockService.java
└── user
    ├── User.java
    ├── UserCoupon.java
    ├── UserCouponCommand.java
    ├── UserCouponInfo.java
    ├── UserCouponRepository.java
    ├── UserCouponService.java
    ├── UserCouponUsedStatus.java
    ├── UserInfo.java
    ├── UserRepository.java
    └── UserService.java
```

### 📌 인프라 레이어 (Infrastructure Layer)

**외부 시스템과의 연동을 처리하는 레이어로, 데이터베이스와의 연동, 외부 API 호출 등을 처리한다.**

도메인 레이어의 `Repository` 인터페이스를 구현하는 레이어이다.
이번 주체 과제에서는 구현체를 만드는 것은 아니라서... 구현 클래스만 만들고 내부 구현은 비워두었다.
(이 부분은 다음 주차에 구현할 예정이다.)

#### 도메인 별 레이어 패키지 구조

```bash
infrastructure
├── balance
│   └── BalanceRepositoryImpl.java
├── coupon
│   └── CouponRepositoryImpl.java
├── order
│   └── OrderRepositoryImpl.java
├── payment
│   └── PaymentRepositoryImpl.java
├── product
│   └── ProductRepositoryImpl.java
├── stock
│   └── StockRepositoryImpl.java
└── user
    ├── UserCouponRepositoryImpl.java
    └── UserRepositoryImpl.java
```

## 😂 이틀만에 과제 수행 

이번 과제는 수요일 부터 시작해서 금요일에 제출했다.
사실 일요일 부터 시작했으나, 작성했던 브랜치를 날려버리고 새로 작성했다. 
클린 아키텍처에 대한 깨달음을 수요일 오전 중에 혼자 깨달았고, 오후 멘토링 시간 이후에 확신을 가졌다. 
주어진 시간이 단 이틀이라... 연차를 사용할 수 밖에 없었다. 

이전에 작성했던 코드는 DIP 를 지키지 못하였고, 작성한 도메인 클래스는 다른 도메인 클래스에 의존하고 있었다.
도메인 서비스의 DTO 인 Command 클래스와 Info 클래스들은 다른 도메인들을 들고 다녔다...

리팩토링으로는 어려운 부분이 많았고, 고민은 많았으나, 결국 새로 작성하기로 했다.

깨달음의 포인트 

+ 도메인 레이어는 다른 도메인 레이어를 알지 못해야 한다.
+ 도메인 클래스이자 엔티티 도메인의 연관관계를 최소화 하여, 다른 도메인과의 결합도를 낮췄다. 
+ 단, 같은 도메인 레이어의 연관관계는 관심사가 비슷하다고 생각하여 협력관계로 인지하고 연관관계를 구성하였다. 
+ 그렇다고, 또 연관관계를 무작정 없애는 것은 아니다. 

## 🤔 고민 포인트

연관관계를 최소화 했지만, 이게 맞나 싶었다. 
예를 들면, 특정 도메인에서 다른 도메인의 식별자를 가지고 있을 때 인스턴스 시에 도메인을 직접 받아야 하는 게 맞는 게 아닌지..?
식별자만 받으면 정합성이 깨지지 않을까? 



