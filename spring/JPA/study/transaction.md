## `@Transactional`  Propagation(전파)

propagation의 옵션

| 옵션 |                                                                                                                                                                                                                                                                                                                                          |
| --- |------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| REQUIRED | Default -  부모 트랜잭션이 존재한다면 부모 트랜잭션 내에서 실행,  없다면 새로운 트랜잭션 생성                                             중간에 자식 / 부모에서 rollback 발생 시, 부모, 자식 모두 rollback                                                                                                                                                                                   |
| REQUIRES_NEW | 무조건 새로운 트랜잭션 생성(시작) 만약 호출한 곳에 이미 트랜잭션이 있다면 기존의 트랜잭션은 메서드가 종료할 때까지 잠시 대기, 본인의 트랜잭션 실행 rollback은 각각 이루어짐                                                                                                                                                                                                                                   |
| MANDATORY | 무조건 부모 트랜잭션에 합류, 이 때 부모 트랜잭션이 없다면 예외 발생                                                                                                                                                                                                                                                                                                  |
| SUPPORTS | 메소드가 트랜잭션을 필요로 하지는 않지만,진행 중인 트랜잭션이 존재하면 트랜잭션을 사용한다는 것을 의미진행 중인 트랜잭션이 존재하지 않더라도, 메소드는 정상적(non-transactional)으로 동작한다.                                                                                                                                                                                                                      |
| NESTED | 부모 트랜잭션이 존재하면 부모 트랜잭션에 중첩, 부모 트랜잭션이 존재하지 않는다면 새로운 트랜잭션을 생성. 부모 트랜잭션에 예외가 발생하면 자식 트랜잭션도 rollback한다. 자식 트랜잭션에 예외가 발생하더라도 부모 트랜잭션은 rollback하지 않는다. 이때 롤백은 부모 트랜잭션에서 자식 트랜잭션을 호출하는 지점까지만 롤백된다. (즉, DB에서 savepoint 기능을 지원해야함 - oracle) 이후 부모 트랜잭션에서 문제가 없으면 부모 트랜잭션은 끝까지 commit 된다. 현재 트랜잭션이 있으면 중첩 트랜잭션 내에서 실행하고, 그렇지 않으면 REQUIRED 처럼 동작합니다. |
| NEVER | non-transactional 로 실행되며 부모 트랜잭션이 존재하면 Exception이 발생한다.                                                                                                                                                                                                                                                                                  |

### `REQUIRED`

![img.png](img.png)![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/ed27a81e-deab-4f25-992a-ad7de91f6212/Untitled.png)

> ref. [https://devlog-wjdrbs96.tistory.com/424](https://devlog-wjdrbs96.tistory.com/424)
>

## Spring에서의 `@Transactional` 에 대한 Default isolation Level

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/2a0648b3-b2ce-4c87-90c3-4e0b29dac463/Untitled.png)

- default 설명

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/7a30686d-a840-46b9-ae34-3535c60f87de/Untitled.png)

→ `DEFAULT`설명을 보면 `JDBC isolation Level` 동일하게 설정.

즉,  `MySQL InnoDB`를 사용 →  `REPEATABLE READ`가 `DEFAULT`로 사용

## Spring Transactional Proxy Pattern

### Proxy Pattern

- 대상 객체의 기능을 대신 수행하는 대리 객체를 사용
- 실제 객체가 아닌 임의의 객체를 생성하여 주입
- JDK Proxy / CGLib Proxy

### JDK Proxy, CGLib Proxy

- JDK Proxy (Dynamic Proxy)
    - Interface가 필요함 (없으면 프록시 생성 불가)
    - Spring AOP default
- CGLib Proxy
    - Class의 byte code를 조작 → 프록시 객체 생성
    - `Extends` 방식으로 프록시 객체 구현
    - JDK 보다 성능 우월 , 최근 Spring Boot 기본 AOP 전략

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/ba93a205-b1f3-4935-a0e4-00068d51b4fd/Untitled.png)

`JDK Proxy`의 경우 : AOP를 적용하여 구현된 클래스의 인터페이스를 프록시 객체로 구현해서 코드를 끼워넣는 방식이다.

Spring은 `@Transactional`이 적용된 모든 클래스/메서드에 대한 `Proxy` 생성

- 프록시는 프레임워크가 트랜잭션을 시작/커밋하기 위해 실행 중인 메서드의 전후로 트랜잭션 로직을 주입

  ![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/7a84f88e-d0f3-475e-b86d-1d1f9737123e/Untitled.png)


왼쪽은 `@Transactional`을 적용하기전 상태,

오른쪽은 `@Trnasactional`이 적용되고 JDK Dynamic Proxy 방식 AOP로 동작했을 때의 모습

**트랜잭션 처리를 다이나믹 프록시 객체에 대신 위임한다.**

다이나믹 프록시 객체는 타깃이 상속하고있는 인터페이스를 상속 후 추상메서드를 구현하며 내부적으로 타깃 메서드 호출 전 후로 트랜잭션 처리를 수행한다.
Controller는 타깃의 메서드를 호출하는것으로 생각하지만 실제로는 프록시의 메서드를 호출하게된다.

(logging 또한 마찬가지)

### private fun → `@Transactional` 적용 불가

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/2b538e31-defe-438a-a3f7-e03cbcd1788e/Untitled.png)

→ proxy 객체에서의 구현 코드 예시

```java
private void createUserListWithTrans(){
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    
    super.createUserListWithTrans();   // private이기 때문에 에러 발생
    
    tx.commit();
}
```

### 같은 클래스 내에서의 여러  `@Transactional` method 호출시 주의

```java
@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void createUserListWithTrans(){
        for (int i = 0; i < 10; i++) {
            createUser(i);
        }
    }

    public void createUserListWithoutTrans(){
        for (int i = 0; i < 10; i++) {
            createUser(i);
        }
    }
    
    @Transactional
    public User createUser(int index){
        User user = User.builder()
                .name("testname::"+index)
                .email("testemail::"+index)
                .build();
        
        userRepository.save(user);
        return user;
    }
```

- jpa가 만든 proxy 객체 Injection

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/38080595-40a9-428e-8e8e-b0aab3743fcf/Untitled.png)

- 중간에 부모에 대해 RuntimeException 발생 시키기

```java
//UserService.java

@Transactional
public void createUserListWithTrans(){
    for (int i = 0; i < 10; i++) {
        createUser(i);
    }
		
    throw new RuntimeException(); // user 생성 완료 후 Exception 발생
}

@Transactional
public User createUser(int index){
    User user = User.builder()
            .name("testname::"+index)
            .email("testemail::"+index)
            .build();
    
    userRepository.save(user);
    return user;
}
```

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/5e4cb72c-06e4-43a1-9757-92567709ebce/Untitled.png)

> **10번의 createUser가 실행됐지만 최종적으로는 0개가 생성된 User**
>

`createUser`에 `@Transaction`이 걸려있어 각각 commit이 될거라 예상했지만 실제 생성된 User는 0개이며 **transaction이 rollback된듯한 결과가 나왔습니다.**

```java
Proxy로 생성한 객체 예상 코드 

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService359caca0 extends UserService {
    private final EntityManager em;

		// Proxy객체에서 UserService의 createUserListWithTrans를 호출하고
    // createUserListWithTrans는 그 안에서 같은 클래스의 createUser()를 호출하기 때문에
    // createUserListWithTrans의 Transaction만 동작하게됨
	
    public void createUserListWithTrans(){
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        super.createUserListWithTrans();
        
        tx.commit();
    }

    public void createUserListWithoutTrans(){
	    super.createUserListWithoutTrans();
    }
    
    public User createUser(int index){
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        
        User user = super.createUser(index);
        
        tx.commit();    
        
        return user;
    }
```

1. Proxy 객체에서 Transaction을 설정
2. 이후 기존 UserService의 `createUserListWithTrans()` 실행
3. UserService의 createUserListWithTrans안에서 같은 클래스 안의 createUser 실행
    - 자식 트랜잭션 실행 → default : **`REQUIRED`**
4. 모든 과정 완료 후 Transaction 종료

Proxy형태로 동작하게 되면 위 과정대로 동작하기 떄문에 최초 진입점인 createUserListWithTrans의 Transaction만 동작하게 되는것입니다.

### `@Test`  + `@Transactional`  = Default (Rollback)

- rollback을 원하지 않을 경우 : `@Rollback(false)` 로 설정 