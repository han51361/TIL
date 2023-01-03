# CQRS 와 EventSourcing

CQRS는 네이밍에서 알 수 있듯이 명령과 쿼리의 역할을 구분 한다는 것이다. 
즉 커맨드`( Create - Insert, Update, Delete : 데이터를 변경)` 와 쿼리`( Select - Read : 데이터를 조회)`의 책임을 분리

### 탄생 배경
- 전통적인 CRUD 아키텍처 기반에서 Application을 개발 및 운영하다 보면 자연스레 Domain Model의 복잡도가 증가되기 마련
- 이로 인해 유지보수 Cost는 지속적으로 증가하게 되며 Domain Model은 점점 설계 시 의도한 방향과는 다르게 변질 되게 된다. 
- 특히나 요즘처럼 고차원적인 UX, 급변하는 Business, 시도때도 없이 달라지는 요구사항을 충족하는 Model을 만드는건 더욱 더 어려운일


이런 일련의 변경사항과 흐름을 곰곰히 관찰해 보니 Application의 Business정책이나 제약(흔히 비지니스 로직이라 부르는것들)은 거의 대 부분 데이터 변경 (C,U,D) 작업에서 처리되고 데이터 조회(R)작업은 단순 데이터 조회가 대 부분
- 이 두 업무를 동일한 Domain Model로 처리하게 되면 각 업무 영역에 필요치 않은 Domain 속성들로 인해 복잡도는 한 없이 증가
-  Domain Model은 애초 설계 의도와는 다른 방향으로 변질될 가능성이 높음 

> 해결방법 : 바로 명령을 처리하는 책임과 조회를 처리하는 책임을 분리 구현 하면 되는거 아닌가? 그렇다 이게 바로 CQRS 인것이다.
> (사실 CQRS는 도메인 주도 개발 - DDD (Domain-driven design) 기반의 Object Model 방법론 적용시 나타났던 문제점들을 해결하기 위해 등장했다고 보는것이 보다 더 명확할 듯)

### 사용 방법

일반적 CRUD System 
![image](https://user-images.githubusercontent.com/27190617/210360635-c71ec71a-4f65-4809-92da-7994c50ad516.png)

전통적 계층에 CQRS 패턴 적용 방법 : 크게 3가지 방법 

### 1. 단일 Data Source에 command / Query 모델을 분리하는 방법

![image](https://user-images.githubusercontent.com/27190617/210360997-7013c909-7714-4fd1-a209-fefb7f939245.png)

- Database(RDBMS) 는 분리하지 않고 기존 구조 그대로 유지
- Model Layer 부분만 Command와 Query Model로 분리하는 수준으로 간단하게 적용. 
- 이렇게 분리된 Model은 각자의 Domain Layer에 대해서 만 모델링하고 코딩하기 때문에 훨씬 단순하게 구현/적용 가능. 
  - 하지만 동일 Database사용에 따른 성능상 문제점은 개선하지 못한다.

### 2. Command용 Database와 Query용 Database를 분리
![image](https://user-images.githubusercontent.com/27190617/210361202-3d7d550c-2c13-4db0-80c3-d742ed0fcec4.png)

- Command용 Database와 Query용 Database를 분리
- 별도의 Broker를 통해서 이 둘 간의 Data sync 처리 
- 해당 방식은 데이터를 조회 하려는 대상 서비스(시스템)들 : 각자 자신의 시스템(서비스)에 맞는 저장소를 선택 가능(폴리글랏 저장 구조)
>  폴리글랏 저장 구조 
>  (참고 : 다수의 Database 혼용하여 사용 하는 것을 폴리글랏 저장소 라고 함)로 구성 할 수 도 있다. 

-  이 경우 각각의 Model에 맞게 저장소(RDBMS, NoSql, Cache)를 튜닝하여 사용할 수 있다는 이점 
-  이는 1)일반 유형에서 거론된 동일 Database사용에 따른 성능 관점의 문제점을 해결 가능. 
-  하지만 동기화 처리를 위한 Broker의 가용성과 신뢰도 보장되어야 함 

### 3. Event Sourcing 적용 
![image](https://user-images.githubusercontent.com/27190617/210361647-615c1155-64e2-4588-9171-2b3efd01af16.png)

이벤트 소싱
- Application내의 모든 Activity를 이벤트로 전환해서 이벤트 스트림(Event Stream)을 별도의 Database에 저장하는 방식
- 이벤트 스트림을 저장하는 Database에는 오직 데이터 추가만 가능
- 계속적으로 쌓인 데이터를 구체화(Materialized)시키는 시점에서 그 때까지 구축된 데이터를 바탕으로 조회대상 데이터를 작성하는 방법을 말한다. 
 - Application내의 상태 변경을 이력으로 관리하는 패턴이 발전된 형태로 볼 수 있음 

`이벤트 소싱의 이벤트 스트림은 오직 추가만 가능하고` +  
`이를 필요로 하는 시점에서 구체화 단계를 거치게 되고`
= 이런 처리 구조가 CQRS의 Model분리 관점과 굉장히 궁합이 잘 맞기에 대부분 CQRS 패턴을 적용하고자 할 때 이벤트 소싱이 적용된 구조를 선택하게 된다.

> 주의) CQRS패턴에 이벤트 소싱은 필수가 아니지만 이벤트 소싱에 CQRS는 필수임
