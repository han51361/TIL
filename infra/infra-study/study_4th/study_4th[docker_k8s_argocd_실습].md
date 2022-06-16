# 4주차 인프라 

## 인프라 실습 [docker image 생성 및 컨테이너 k8s와 argocd 이용하기 ]

### `hello-controller` 프로젝트 생성 

```java 
@RestController
public class HelloController {
    
    @RequestMapping("/hello-world")
    public String hello(){
        return "hello, world";
    }
}

```

### Dockerfile 작성 
프로젝트 최상위 위치에 `dockerfile` 새로 생성 

```dockerfile
FROM openjdk:8-jdk-alpine
ARG JAR_FILE=build/libs/infra-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```
- java version에 맞춰서 작성 
- `JAR_FILE`에는 jar 파일이 위치한 경로 값 추가
- 다시 build 

> 이어서 command line 
```shell
docker build -t han51351/infra . #도커 빌드 {도커 허브 내 유저 이름과 동일한 이름}/image 이름 
docker run -d -p 8080:8080 han51351/infra 
docker ps #프로세스 넘버 확인용 
docker exec -it 65544c0868bab11626c13 /bin/bash or /bin/sh 
docker login 
docker push han51361/infra #docker hub repository 
docker image rm han51361/infra 
docker pull han51361/infra #docker image pull 
```

![img.png](img.png)

### minikube 설치 
[minikube url](https://minikube.sigs.k8s.io/docs/start/)

minikube start cluster
```shell
minikube start
```

minikube dashboard 
```shell
minikube dashboard
```

###deployment & service yaml 작성해보기 
pod을 띄우기 전에 k8s에 띄울 yaml file 작성 

두 가지 yaml파일은 프로젝트 내에 `src` 디렉토리와 동일한 레벨에 `k8s` 디렉토리 생성하여 저장 

argocd에서 제공하는 deployment , svc yaml 예시 파일을 수정하기 

argocd github : [url](https://github.com/argoproj/argocd-example-apps/tree/master/guestbook)

- deployment.yaml
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: infra
spec:
  replicas: 1
  revisionHistoryLimit: 3
  selector:
    matchLabels:
      app: infra
  template:
    metadata:
      labels:
        app: infra
    spec:
      containers:
        - image: han51361/infra:latest
          name: infra-app
          ports:
            - containerPort: 8080
```
- service(svc).yaml
```yaml 
- apiVersion: v1
kind: Service
metadata:
  name: infra
spec:
  ports:
    - port: 8080
      targetPort: 8080
  selector:
    app: infra
 ```


### k8s에 작성한 svc, deployment yaml 적용하기 
```shell

# 확인 
kubectl get pod 
kubectl get deployment 
kubectl get svc 
kubectl describe pod {pod_name}

#생성(yaml 적용) 
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml 

#삭제 
kubectl delete deployment {배포이름}
kubectl delete svc {서비스이름}
kubectl delete pod {pod이름}

#log 확인 
kubectl logs -f {pod_name}
# pod 내부 확인 
kubectl exec -it {pod name} -- /bin/bash  
                              or /bin/sh 
                              
# port-forwarding 
kubectl port-forward svc/{svc-name} 8080:8080                      
```

yaml apply 후 pod running 
![img_1.png](img_1.png)


### argocd 설치 및 적용

argocd 설치 
```shell
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
```

argocd cli 설치 
```shell
brew install argocd
```

argocd api 서버 접근 

- argocd 서버 서비스 타입 `LoadBalancer` 로 변경 
```shell
kubectl patch svc argocd-server -n argocd -p '{"spec": {"type": "LoadBalancer"}}'
```

- argocd server port-forwarding 
```shell
kubectl port-forward svc/argocd-server -n argocd 8090:443
# 8080은 pod container에서 사용중 
```

- argocd UI 접속 
> localhost:8090 url 접속 

- login 
1. id : admin 입력
2. terminal에 커멘드 입력 후 패스워드 획득 및 입력 
```shell 
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d; echo
``` 

### argocd application 추가 

- new app 을 누른다. 
- application 이름 입력 및 부가사항 설정 
- source 
  - repository url : 프로젝트, dockerfile 및 yaml 작성한 내용을 git repo에 푸쉬 후  url 등록 
- 이 때 namespace가 없다면 add namespace 항목 체크 
- destination 
  - cluster : `https://kubernetes.default.svc` or `in-cluster` for cluster name 입력
  - namespace : default 

### Sync 
생성된 어플리케이션 눌러서 sync 버튼 누르면 sync 
