# 도커와 쿠버네티스 첫 걸음 

## 2.1 도커를 사용한 컨테이너 이미지 Control 
- k8s는 워커 노드를 하나의 플랫폼으로 제공 
- 워커 노드들은 파드 조합들로 구성
  - 파드 : 1개 이상의 컨테이너 그룹
  - 도커 : 컨테이너화할 수 있는 가상화 플랫폼 

### 2.1.1 HelloWorld Container 실행 
이미지 푸시하기 
 - 이미지를 푸시하기 전 도커 허브웹 UI에 플젝 생성

도커 클라이언트 로그인 

```shell 
$ docker login
```

이미지에 태그 지정

```shell
$ docker tag ${YOUR_IMAGE}:${TAG} 
```

이미지 푸시 
```shell
$ docker push [OPTIONS] NAME[:TAG]
// 커맨드
docker run busybox echo "Hello world"
// 실행
Hello world
```
- 커멘드를 실행하면서 백그라운드에서 일어나는 동작원리
1. 도커는 busy:box:lates 이미지가 로컬에 존재하는지 체크 
2. 존재하지 않는다면 도커는 허브 레지스트리에서 이미지를 다운로드 
3. 다운로드된 이미지에서 도커는 컨테이너 생성 및 컨테이너 내부에서 커멘드 실행 

다른 이미지 실행
```shell
docker run <image>
```

컨테이너 이미지에 버전 지정 
```shell
docker run <image>:<tag> // tag 지정하지 않으면 latest
```

### 2.1.3 이미지를 위한 Dockerfile 생성 (2.1.2 js 어플리케이션 생성 생략)
```dockerfile
FROM node:7
ADD app.js /app.js
ENTRYPOINT ["node", "app.js"]
```
- FROM 줄은 시작점으로 사용할 컨테이너 임지를 정의 
- 두 번째 줄은 로컬 디렉토리 app.js 파일을 이미지의 루트 디렉토리에 동일한 이름으로 추가한다. 
- 마지막 줄에서는 이미지를 수행했을 때 수행되어야할 커멘드 정의 

### 2.1.4 컨테이너 이미지 생성 
이미지를 빌드하기 위한 (Docker,app.js)가 완료되었으므로 아래 명령어를 통해 이미지를 빌드 
```shell 
docker build -t kubia .
```
- 도커에게 현재 디렉토리 컨텐츠 기반 kubia라고 부르는 이미지 빌드 요청
- 도커는 디렉토리 내 dockerfile을 살펴보고 파일에 명시된 지시 사항에 근거하여 이미지 빌드
  - 빌드 프로세스는 도커 클라이언트가 수행하지 않음 
  - 디렉토리의 전체 컨텐츠가 docker daemon에 업로드 되고, 그곳에서 이미지 빌드 
  - 도커 클라이언트와 데몬은 같은 머신에 있어야 할 필요 X 

- 이미지 레이어 
  - 이미지는 여러 개의 레이어로 구성 
  - 서로 다른 이미지가 여러 레이어를 공유할 수 있음 
    - 따라서 저장 및 전송에 효과적 
  
앞선 ```docker build kubia . ``` 이미지 레이어 예시 설명 
1. 도커 크라이언트가 디렉터리 컨텐츠를 데몬에 업로드 
2. 이미지가 로컬에 저장되어있지 않는 경우, 도커가 이미지 pull 
3. 새로운 이미지를 build
- 각 dockerfile이 새로운 레이어를 하나만 생성하는 것이 X 
- 이미지를 빌드하는 동안, 기본 이미지의 모든 레이어를 가져온 다음, 그 위에 새로운 레이어 생성, app.js 파일을 그 위에 추가 
- 그 후, 이미지를 실행할 떄, 수행해야할 명령어를 지정하는 또 하나의 레이어를 추가
  - 이 마지막 레이어는 ```kubia:latest``` 라고 태그 지정 

- 이미지 빌드 프로세스가 완료되면 새로운 이미지는 로컬에 저장
이미지 조회 
```shell 
$ docker images
REPOSITORY   TAG       IMAGE ID       CREATED          SIZE
kubia        latest    06ed9d19d3e4   15 minutes ago   660MB
redis        latest    cc69ae189a1a   7 days ago       105MB
busybox      latest    491198851f0c   11 days ago      1.23MB
postgres     latest    1f0815c1cb6e   2 weeks ago      314MB
mysql        5.7       5f47254ca581   2 weeks ago      449MB
mysql        8.0       2933adc350f3   2 weeks ago      546MB
mysql        latest    2933adc350f3   2 weeks ago      546MB
```

### 2.1.5 컨테이너 이미지 실행 
```shell 
docker run --name kubeia-container -p 9998:9998 -d kubia
# 도커가 kubia 이미지에서 kubia-container 이름의 새로운 컨테이너를 실행하게 한다.
```
- ```--name kubia-container``` : 컨테이너 이름 
- ```-p 8080:8080 ``` : port forwarding
- ```-d``` : 백그라운드 실행 
- ```kubia``` : 이미지 이름 

애플리케이션 접근
```shell
curl localhost:8080
you've hit 44d76963e8e1
```  
실행중인 모든 컨테이너 조회 
```shell
docker ps
docker ps -a // 중지된 컨테이너까지 조회
docker inspect kubia-container // 자세한 정보 json 출력 
```  

### 2.1.6 실행중인 컨테이너 내부 탐색 
하나의 컨테이너 여러 프로세스가 실행될 수 있으므로 추가 프로세스를 실행해 컨테이너 내부를 살펴볼 수 있다.

컨테이너 내부 셸 실행 
```shell
docker exec -it kubia-container bash
```  
- kubia-container 컨테이너 내부에 bash 실행 
- 일반적인 셸을 사용하는 것과 동일하게 셸을 사용하고 싶다면 두 옵션 추가 
  - -i : 표준입력(STDIN) 오픈 상태 유지 , 셸에 명령어 입력하기 위해 필요 
  - -t : 의사(pseudo) 터미널 할당 

컨테이너 내부에서 프로세스 조회 
```shell
ps aux
```  
- 호스트 운영체제와 컨테이너 내부에서 조회한 프로세스의 ID는 다름
  - 컨테이너는 자체 리눅스 PID 네임스페이스를 사용
  - 고유의 시퀀스 번호를 가지고 완전히 분리된 프로세스 트리를 지님  

### 격리된 컨테이너 파일시스템
격리된 프로세스를 가진 것과 마찬가지로 각각의 컨테이너는 격뢰된 파일시스템을 가짐 

### 2.1.7 컨테이너 중지와 삭제 
```shell
docker stop kubia-container // 컨테이너 중지
docker rm kubia-container // 컨테이너 삭제
```
### 2.1.8 이미지 레지스트리에 이미지 푸시 
도커허브는 이미지의 레포지터리 이름이 도커 허브 ID로 시작해야만 이미지 푸시 가능 
> 추가 태그로 이미지 태그로 이미지 태그 지정 
- ex) kubia -> [도커 허브 ID]/kubia로 추가적인 태그 생성 
```shell
docker tag kubia luksa/kubia
```

도커 허브에 이미지 푸시하기 
1. docker 로그인 
2. docker 이미지 push 
```shell
docker push [도커 허브 ID]/kubia
```
다른 머신에서 이미지 실행하기 
```shell
docker run -p 8080:8080 -d [도커 허브 ID]/kubia
```

## 2.2 쿠버네티스 Cluster 설치 
Minikube는 local 내에서 k8s 테스트 및 어플리케이션 개발할 수 있는 단일 노드 클러스터를 설치하는 도구 

### 클러스터내 노드 조회 및 동작 확인 
- ```kubectl ``` : client 명령어를 마스터 노드에서 실행중인 API SERVER로 rest 요청을 통해 클러스터와 상호작용함
```shell
$ kubectl get nodes
NAME                      STATUS  AGE  VERSION
gke-kubia-85f6-node-0rrx  Ready   1m    v1.5.3
gke-kubia-85f6-node-heo1  Ready   1m    v1.5.3
gke-kubia-85f6-node-vs9f  Ready   1m    v1.5.3
```

### 오브젝트 세부 정보 조회 
```shell 
$ kubectl describe node gke-kubia-85f6-node-0rrx 
# 특정 노드 이름을 명시하면 노드 정보 출력
$ kubectl describe node
# 모든 노드 정보 상세 출력
```

## 2.3 쿠버네티스에서 어플리케이션 실행하기 

### 2.3.1 Node.js 어플리케이션 구동
```shell 
$ kubectl run kubia --image=luksa/kubia --port=8080 --generator=run/v1
replicationcontroller "kubia" created
# kubia라는 레플리케이션 컨트롤러가 실행된다. 
```
- ```--image=luksa/kubia``` : 컨테이너 이미지 명시 
- ```--port=8080 ```: 쿠버네티스 어플리케이션 8080 포트 수신 대기 지정 
- ```--generator=run/v1``` : (잘 사용 안함) k8s에서 디플로이먼트 대신 레플리케이션 컨트롤러를 생성하기 때문에 사용  현재는 generator는 더이상 사용할 수 없ㅇ므 

파드 소개 
- k8s는 개별 컨테이너들을 직접 다루지 않음. 대신 함께 배치된 다수의 컨테이너라는 개념 
- pod : 컨테이너의 그룹 
- pod은 하나 이상의 밀접하게 연관된 컨테이너의 그룹으로 같은 워커 노드에서 같은 네임스페이스로 함께 실행 
- 각 파드는 자체 ip, hostname, process 등 논리적으로 분리된 머신 
- 파드는 다른 워커 노드에 널리 퍼져있음 

파드 조회 
```shell
kubectl  get pods // pod 조회
kubectl  decribe pod <name> // 세부 정보 조회
```
> ### 백그라운드 동작 설명
> 
> 1.이미지를 빌드한 도커 허브에 푸시 (로컬에 빌드한 이미지는 로컬에서만 사용할 수 있음 따라서 도커 데몬이 실행중인 다른 워커 노드에 컨테이너 이미지를 접근하게 하려면 해당 절차 필수)
> 
> 2.kubectl 명령어를 통해 쿠버네티스 API 서버로 rest http 요청 전달, 클러스터에 새로운 레플리케이션컨트롤러 오브젝트 생성
> 
> 3.레플리케이션 컨트롤러는 새 파드 생성하고 스케줄러에 의해 워커 노드중 하나에 스케줄링 
> 
> 4.해당 워커노드에 Kubelet은 파드가 스케줄링된 것을 보고, 이미지가 로컬에 없기 떄문에 도커에서 레지스트리에 특정 이미지 pull 지시 
> 
> 5.이미지를 다운로드 후 , 도커는 컨테이너 생성 및 실행 


### 2.3.2 웹어플리케이션 접근하기 
서비스 오브젝트 생성 
- 쿠버네티스에게 앞서 생성한 레플리케이션컨트롤러를 노출하도록 명령 
```shell
$ kubectl expose rc kubia --type=LoadBalancer --name kubia-http
service "kubia-http" exposed
```

서비스 조회 
- expose 명령어의 출력 결과 -> kubia-htp 라는 서비스 표시 
- 서비스는 파드나 노드같은 오브젝트로서, 새로 생성된 오브젝트 
```shell
$ kubectl get services (k get svc)
# 미니 쿠베는 mk service kubia-http
NAME         CLUSTER-IP     EXTERNAL-IP   PORT(S)         AGE
kubernetes   10.3.240.1     <none>        443/TCP         34m
kubia-http   10.3.246.185   <pending>     8080:31348/TCP  4s
```

### 2.3.3 시스템의 논리적인 부분 
레플리케이션컨트롤러, 파드, 서비스가 서로 동작하는 방식의 이해 
1. kubectl run 
2. 레플리케이션컨트롤러를 생성
3. 레플리케이션컨트롤러가 실제 파드 생성
4. 클러스터 외부에서 파드를 접근하게 하기 위해 쿠버네티스에게 모든 파드를 단일 서비스로 노출하도록 지시 

파드와 컨테이너의 이해 
- 파드는 자체의 고유한 private ip(cluster-ip)와 hostname을 가짐 

레플리케이션컨트롤러의 역할의 이해 
- 보통 파드를 복제(replica)하고 항상 running 상태로 만듦
- 파드의 레플리카를 지정하지 않으면 하나만 생성
- 파드가 종료되거나 정상동작하지 않는다면, 해당 파드를 대체하기 위해 새로운 파드 생성 

서비스가 필요한 이유에 대하여 
- 파드는 일시적 혹은 언제든 사라질 수 있음
  - 이러한 상황에서 레플리케이션컨트롤러에 의해 새로운 파드로 대체되고 새로운 파드는 새로운 IP주소 할당 받음 
  - 그렇기 때문에 서비스가 생성되면 정적IP를 할당받고 서비스가 존속하는 동안에는 변경되지 않음
  - 즉, 서비스는 동일한 서비스를 제공하는 하나 이상의 파드 그룹의 정적 위치를 나타내는 역할을 한다. 

### 2.3.4 어플리케이션 수평 확장 
레플리카 수 늘리기 
```shell
$ kubectl scale rc kubia --replicas=3

replicationcontroller "kubia" scaled
```

서비스 호출 시 모든 파드가 요청을 받는지 확인 
- 요청이 무작위로 다른 파드를 호출 
  - ```shell
     $ curl 104.155.74.57:8080
     You've hit kubia-hczji
     $ curl 104.155.74.57:8080
     You've hit kubia-iq9y6
     $ curl 104.155.74.57:8080
     You've hit kubia-iq9y6
     $ curl 104.155.74.57:8080
     You've hit kubia-4jfyf
    ```

- 하나 이상의 파드가 서비스 뒤에 존재할 때, 서비스는 로드밸런서의 역할을 수행 

### 2.3.5 어플리케이션이 실행 중인 노드 검사 
- 어떤 노드에 파드가 실행 중인지는 중요치 않음
- 파드가 스케줄링된 노드와 상관없이 동일한 유형의 OS 환경을 가짐 

파드를 조회할 때 파드 IP와 실행 중인 노드 표시
```shell
kubectl get pods -o wide
#세부정보 보기 
kubectl describe pod kubia-hczji
```

### 2.3.6 k8s 대시보드 
minikube 에서 대시보드 접근 
```shell
#세부정보 보기 
minikube dashboard
```
