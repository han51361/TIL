# R-Tree Index (Spatial-Index) 
**공간 인덱스** : 
- R-Tree 인덱스 알고리즘을 이용해 **2차원의 데이터**를 인덱싱/검색하는 목적의 인덱스
- 내부 메커니즘은 B-Tree와 유사 
- 차이점 : 
  - B-Tree : 인덱스를 구성하는 칼럼의 값 : 1차원의 스칼라 값
  - R-Tree : 2차원의 공간 개념 값 

위치 기반 서비스 구현 방법에 **공간확장** 이용
- MySQL 공간 확장 기능
  - 공간 데이터를 저장할 수 있는 데이터 타입
  - 공간 데이터의 검색을 위한 공간 인덱스(R-Tree 알고리즘) 
  - 공간 데이터의 연산 함수(거리 또는 포함 관계 처리) 

## 8.4.1 구조 및 특성
- 공간 정보 저장 및 검색을 위한 여러 Geometry 정보 관리 데이터 타입 제공
- 데이터 타입 : Point, LINE, POLYGON, GEOMETRY(앞 3개 타입의 super type) 
  - 공간 데이터는 (x,y) 좌표만 존재한다면 하나의 도형 객체 가능 
![image](https://github.com/xonmin/TIL/assets/27190617/d2dd4494-9606-4a58-8702-eb98d898065d)
- MBR(Minimum Bounding Rectangle) : 해당 도형을 감싸는 최소 크기의 사각형 
![image](https://github.com/xonmin/TIL/assets/27190617/340f125a-bea9-4ce8-b184-698a693273b3)
- R-Tree : MBR들의 포함 관계를 B-Tree 형태로 구현한 인덱스 

MBR 레벨 단위 구별 & Spatial(R-Tree) 인덱스 구조 
![image](https://github.com/xonmin/TIL/assets/27190617/4a1ccf62-add3-4b45-860d-b2dfd4e8f407)
- 최상위 레벨 : R1, R2 - R-Tree 루트 노드에 저장되는 정보
- 차상위 레벨 : R3 ~ R6(중간 크기의 MBR - 도형 객체의 그룹) - R-Tree 브랜치 노드 
- 최하위 레벨 : R7 ~ R14(개별 각 도형 데이터의 MBR)  - R-Tree 리프 노드 
![image](https://github.com/xonmin/TIL/assets/27190617/28f2d52d-6788-498f-99e0-8e09305abfeb)

## 8.4.2 R-Tree 인덱스 용도 
- 일반적으로 WGS84(GPS) 기준의 위도, 경도 좌표 저장에 주로 사용 (CAD/CAM SW / 회로 디자인의 좌표 시스템 정보에 모두 적용 가능) 
- R-Tree : 각 도형(도형의 MBR) 포함관계를 이용하여 만들어진 인덱스 
  - `ST_Contains() ST_Within()`와 같은 포함 관계를 비교하는 함수로 검색을 수행하는 경우에만 인덱스 이용 가능 
  - ex) 사용자 위치 기준 반경 5KM 음식점 검색 
> 거리 비교 함수 `ST_Distance() / ST_Distance_Sphere()` 함수는 공간 인덱스를 효율적으로 사용 못함 

ex) 
![image](https://github.com/xonmin/TIL/assets/27190617/d891fabe-d4cd-4b1f-9cec-29fadf4f3464)
- P 기준 : `ST_Contains() ST_Within()` 를 통해 반경 5KM 를 그리는 원의 MBR로 포함 관계 비교 수행 
- P6은 기준에는 충족되지 않지만, MBR에는 포함되는데, P6을 제외하는 결과를 위해서는 별도 비교 필요 (만약 포함되도 상관없다면, 해당 함수 사용을 통해 조회 가능)

```sql 
// A(포함 경계)에 포함되는 좌표 Px만 검색
sql> SELECT * FROM tb_location WHERE ST_Contains(A, px);

sql> SELECT * FROM tb_location WHERE ST_Within(px, A);

// P6 제거 쿼리 
sql> SELECT * FROM tb_location WHERE ST_Contains(A, px) AND ST_Distance_Sphere(p, px) <= 5*1000 // 5km
```
