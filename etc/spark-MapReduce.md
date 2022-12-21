# MapReduce vs Spark 

![image](https://user-images.githubusercontent.com/27190617/208903155-784b4754-ced1-4347-a08c-844d935c7528.png)
< 좌 : mapreduce / 우 : spark / Tez > 

### 성능 (Latency, Speed)

Spark는 메모리 내에서 처리 

MapReduce : 디스크 read/write  

- 따라서 처리 속도 Spark * 1000 ≤ MapReduce
- 가용 데이터 크기 : MapReduce ≥ Spark

MapReduce 엔진 : HDFS 중간 결과 작성 

Spark : 중간결과를 작성하지 않으며, 각 실행 그래프를 크게 최적화 

- 이 때 다른 최적화 전략으로는 실행 그래프 전체에 걸쳐 로컬 노드의 메모리 캐싱

### 공수 측면

Spark : 기본 구조로 RDD 사용 / 고수준 연산 작성 수월 

언어 지원 : Scala, Java, Python, R

> RDD: Resilient Distributed Dataset 의 줄임말로 스파크의 기본 데이터 구조이다.
(분산되어 있는 데이터에 에러가 생겨도 복구할 수 있는 능력 / 클러스터의 여러 노드에 데이터를 분산에서 저장되는 데이터)
분산 변경 불가능한 객체 모음이며 스파크의 모든 작업은 새로운 RDD를 만들거나 존재하는 RDD를 변형하거나 결과 계산을 위해 RDD에서 연산하는 것을 표현하고 있다
> 
- 고수준 연산 : map , flatMap, filter, intersection …

MR : 개발자가 구현해야할 코드 상대적 많음, 저수준 코드 레벨 작성  

### 범용성

Spark : Batch, Interactive, ML, Streaming, realtime-analysis 등 가능 

- 스케줄링: in-memory 기반이기 때문에 spark 내부적 스케줄러를 통해 동작
![image](https://user-images.githubusercontent.com/27190617/208903246-47ff92d0-de1d-4c1a-8b6d-2da213e48c00.png)


MR : Batch(일괄처리)에 대해서만 처리 

- 스케줄링을 위해서는 외부적 스케줄러가 필요 (ex_ Hadoop Oozie)

### 회복성 및 내결함성

Spark : RDD 는 DAG 재계산을 통해 장애가 발생한 노드의 파티션을 복구 가능,

- cf) RDD 종속성을 줄이기 위해 Hadoop 과 마찬가지로 복구 지원

MR : 내부적으로 여러 노드에 복제되기 때문에 내결함성 존재 

- 작업을 수행하는 동안 실패하면 다시 시작할 때 중단된 위치에서 다시 시작됩니다. 
맵리듀스는 하드디스크를 기반으로 하기 때문에 작업 중간에 실패해도 제자리를 유지할 수 있습니다.
