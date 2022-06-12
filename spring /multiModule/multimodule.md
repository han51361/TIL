# 멀티 모듈에 대하여 

## 멀티 모듈이란 
서로 독립적인 프로젝트(인증, 어플리케이션)를 하나의 프로젝트로 묶어 모듈로서 사용되는 구조 
- 멀티 모듈을 사용하면 공통적인 기능을 모아 하나의 모듈로 만드는 것이 가능
- 즉, 인증과 어플리케이션에서 공통으로 사용하는 `util` , `domain`, `repository` 등을 모듈로 분리해 사용할 수 있음 
- 
## 멀티 모듈 프로젝트란 
동일한 도메인에 서로 독립된 프로젝트 단위로 아래 세가지 프로젝트를 진행한다면 
- external-api  
- infra  
- batch 

> 가장 큰 문제점
> 
> 시스템의 중심 domain 이 가져야할 구조 및 규칙 등을 동일하게 보장해주는 메커니즘이 없다.  

따라서 개발자는 동일한 도메인을 가지고 있는 3가지 어플리케이션을 ctrl + c , ctrl + v 하며 개발해야 함 

### 멀티 모듈 프로젝트를 통해 귀찮음과 안전성을 보장하자
앞서 별도의 프로젝트로 관리하는 것이 아닌, 하나의 시스템에서 중심 도메인을 모듈로 분리하여 다른 모듈이 도메인에 접근하여 같은 보장 메커니즘을 제공받을 수 있도록 합니다. 
- 멀티 모듈 프로젝트는 공통으로 사용하는 코드들을 모아놓고 같이 사용할 수 있게끔 해준다. 

## 실패한 멀티 모듈 프로젝트 
공통으로 사용하는 코드들을 모은 것 모듈을 `common` 으로 지었다고 가정 

![image](https://user-images.githubusercontent.com/27190617/173220837-84da19e7-ce75-4a4b-8481-8f34a09bfa26.png)


### 공통(Common) 모듈의 저주 
- Common 모듈에는 대부분의 핵심 또는 공통되는 코드들이 모두 들어가 있게 되었을 떄 
- 다양한 어플리케이션에서 기능들이 추가되면서 공통 모듈의 크기는 계속해서 커진다. 
> 결국 어플리케이션에서 하는 일은 점점 줄어들고 공통 모듈에서 점점 더 많은 일을 하게 됨 

스파게티 코드 
- 모든 프로젝트들은 주기적인 청소가 반드시 필요하다(기능의 F/O , 리펙토링 등). 하지만 공통 모듈은 이를 방해한다. 
  - Q. 리펙토링의 영향 범위는 어디까지인가?
  - A. 시스템 전체 (코드는 결국 모두 결합도가 높아서 하나의 코드 수정은 전체가 영향받는 것이 불가피하다)
```java
class AService {
  private final ARepository aRepository;

  public A act(Long id) {
    ...
    return aRepository.findById(id);
  }
}

class BService {
  private final AService aService;

  public B act(Long id) {
    A a = aService.act(id);
    ...
    return mapToB(a);
  }
}

class CService {
  private final BService bService;

  public C act(Long id) {
    B b = bService.act(id);
    ...
    return mapToC(b);
  }
}

class DService {
  private final CService cService;

  //띠용!!
  public A act(Long id) {
    C c = cService.act(id);
    ...
    return mapToA(c);
  }
}
```

의존성 덩어리 
- 공통 모듈은 결국 전부에 대한 의존성을 갖기 마련
  - 전부가 아니더라도 프로젝트에서 사용하는 대부분의 의존성이 공통 모듈로부터 시작될 것 
- 문제는 어플리케이션들이 사용하는 의존성이 다를 수 있다. 
  - ex) DB를 사용하지 않는 어플리케이션이 공통 모듈을 위해 db와 connection을 맺게되는 의존성 
  - 결국 이거는 장애 포인트가 될 것 
![image](https://user-images.githubusercontent.com/27190617/173220850-75471dc3-a9a1-4d8a-84c2-1aa4c549e359.png)

공통 설정 
- 설정까지 공통 모듈로 몰게 되는 경우도 많이 보인다. 
- 고정적으로 공통으로 사용되어야 하는 호스트 정보 등은 경우에 따라 공통으로 보아야할 수도 있지만,
그 외의 `Thread Pool`, `Connection Pool`, `Timeout` 등의 설정도 가장 민감하고 중요한 어플리케이션 기준으로 몰아 들어가게 된다. 
  - ex) 발생하는 대표적인 문제 : DB 커넥션 
    - 최대 커넥션 개수는 고정 하지만 공통 모듈에 의한 불필요한 커넥션 발생으로 인해 장애 유발 

## 멀티 모듈 구성하기 
모듈 : 독립적으로 운영될 수 있는 의미를 가지는 구성요소 단위 
