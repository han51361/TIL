# Service Discovery

### 배경 :

- MSA 분산 환경 : 서비스 간 원격 호출 구성 (일반적으로 원격 서비스 호출 - IP / port 이용 )
- 클라우드 환경 내에서 오토 스케일링 등에 의해 동적 생성, 컨테이너 기반 배포 등으로 서비스 IP가 동적으로 자주 변경될 수 있음

![image](https://user-images.githubusercontent.com/27190617/198023541-4dbbc1f2-26d1-461a-9a6b-58fb376e8afa.png)
- 또한 책에 나온 것 처럼 파티션 리벨런싱에 의해 노드에 할당된 파티션이 바뀌는 상황 또한 결국 dest 노드를 찾아야하는 상황 → (service discovery가 필요)

> 따라서 클라이언트가 서비스를 호출하기 위해서는 서비스의 위치(or Ip/Port) 를 알아야하는데, 
이를 보통 `service registry` 에 등록해놓음
> 

![image](https://user-images.githubusercontent.com/27190617/198023597-f2afd324-58e6-4794-b2ae-e9bcde9cffc9.png)
### Service Registry

> 서비스 디스커버리의 가장 중요한 포인트이며, 이 때문에 높은 가용성 및 늘 최신성을 보장해야함
> 

Ex) 

- Netflix - Eureka
    
    ```gradle
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-server’
    ```
    
    - 서비스 인스턴스를 등록하거나 query 할 수 있도록 rest API 제공
    - `POST` 를 통한 등록 / `PUT` 을 통한 30초마다 해당 등록 refresh
        
     ![image](https://user-images.githubusercontent.com/27190617/198023640-246f80d3-2a5e-42c4-a86e-9559b08627c2.png)
     
> 새로운 인스턴스는 시작할 때 Eureka 서버에 IP, 호스트 주소, 포트 정보 등을 스스로 전송
>
> Eureka 서버는 받은 정보를 가지고 일정한 간격으로 상태를 체크하면서 해당 인스턴스를 관리
>
> 인스턴스가 새로 실행될 때마다 자신의 정보를 서버에 동적으로 등록하기 때문에, 
> 운영자들은 서비스를 수평 확장할 때 IP 정보에 대해 신경 쓸 필요  X
>(여기서 수평 확장이란 부하가 걸린 서비스에 인스턴스를 늘려 처리량을 높이는 것을 의미합니다.) 
>
> 운영자들은 설정 파일에 Eureka 서버 정보만 입력하면 되고, 서비스들은 다른 서비스를 호출할 때 Eureka 서버에 등록된 인스턴스를 조회하면 됩니다.



- **Apache Zookeeper**
    - 분산된 애플리케이션을 위해 일반적으로 널리 사용되는 고사양 코디네이션 서비스
    - Hadoop world에 originated됨
    - Hadoop 안의 다양한 component 유지에 Good
    - Server Cluster가 running되면 모든 node 사이의 설정값을 zookeeper가 공유함

- **etcd**
    - Http를 통해 접근 가능, 고가용성, 분산, 일관성의 특징인  key-value 저장소
        - 현재 kubernetes에서도 사용 중
    - deploy, setup, user가 쉬움 (일반적으로 zookeeper와 비교했을 때도 쉬움)
    - 그러나 automatic service discovery를 위해선 Registator와 같은 Third Party tool들과 함께 써야함

- **Consul**
    - Hierachical key-value 저장소
    - Data 저장 뿐만 아니라 Watches Registration 또한 해줌
        - (데이터 변경에 대한 noti 전송, health check, custom command 등 여러 task에 사용됨)
    - 서비스를 등록하고 찾기위한 DNS interface,  API를 제공
    

## MSA 패턴 중 service discovery 패턴을 구현하는 대표적인 방법 2가지


### **Client-side Discovery  VS Service-side Discovery**

**Client-side Discovery**

- client가 Service Registry에서 서비스의 위치를 찾아서 로드밸런싱 알고리즘을 통해 서비스 호출
- service Instance는 구동될 때 `service registry` 에 본인의 위치를 알리고, 종료 시에는 `service registry` 에서 삭제
- 서비스 인스턴스 등록은 heartbeat 메커니즘에 따라 주기적으로 refresh

> ex) Netflix Eureka - Client-Side Discovery Pattern 방식의  Service Registry

![image](https://user-images.githubusercontent.com/27190617/198023815-8ff29a6c-cd06-49ce-a653-2b101abd487c.png)


**장단점** : 

- 비교적 간단하다
- 클라이언트가 사용가능한 서비스 인스턴스에 대해 알고 있기 때문에, 각 서비스별 로드 밸런싱 방법 선택 가능
- 클라이언트와 서비스 레지스트리가 연결되어있어 결국 디펜던시가 존재한다.
- 서비스 클라이언트에서 사용하는 언어, 프레임워크에 따라 클라이언트 측에서 검색 로직 구현에 대해 의존성이 있다.

**Server-side Discovery**

호출시 해당 호출(요청)을 로드 밸런서(or proxy server)를 통해 서비스로 전달하는 방식 

요청을 받은 로드 밸런서는 service discovery로 부터 등록된 서비스의 위치를 응답으로 받아, 이를 기반으로 요청을 라우팅 처리한다. 

![image](https://user-images.githubusercontent.com/27190617/198023862-2436ee2e-7b6f-4303-a441-e628252b087c.png)


ex) 
- service discovery(loadbalancer, proxy server) : aws ELB, Google LoadBalancer
- Service Registry : Kube-DNS + etcd

**장단점** : 

- discovery에 대한 정보가 클라이언트와 분리되어있어 의존성이 떨어진다.
- 클라이언트는 단순히 로드 밸런서에 요청만 한다. → 언어, 프레임워크에 맞게 검색 로직에 대한 구현 강제성 X
- 일부 배포환경에서는 이 기능을 현재 무료로 제공하고 있다.
- 단, 로드밸런서가 배포환경에서 제공되어야 한다
    - 제공되어있지 않다면 설정 및 관리해야하는 또 하나의 스텝이 필요하다
