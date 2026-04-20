# Seller Insight

판매자 데이터를 수집·정규화·분석해 다음 행동을 추천하는 커머스 인사이트 백엔드 시스템

## 프로젝트 소개

이 프로젝트는 처음에 `네이버 커머스 API 기반 판매자 AI 피드백 서비스`로 시작했다.  
판매자 관리 화면에 흩어진 주문, 상품, 고객, 유입 데이터를 하나로 모으고, 판매자의 다음 행동을 제안하는 서비스를 만드는 것이 목표였다.

하지만 개발 과정에서 네이버 커머스 API 실연동에는 다음과 같은 현실적인 제약이 있다는 점을 확인했다.

- 솔루션 등록 및 키 발급 절차 필요
- `SELLER` 토큰 발급을 위한 `accountUid` 연동 필요
- 개인 자격 기준으로는 실연동 검증이 제한적
- 일부 분석 API는 브랜드스토어 전용 또는 추가 구독 필요

그래서 프로젝트를 중단하지 않고, 방향을 아래와 같이 재정의했다.

> 외부 커머스 플랫폼 연동을 고려해 판매자 데이터를 정규화하고, 일별 지표와 규칙 기반 인사이트를 생성해 판매자의 다음 행동을 추천하는 커머스 인사이트 백엔드 시스템

즉, 특정 플랫폼 실연동 자체보다 `백엔드 분석 시스템 설계와 구현`에 초점을 맞춘다.

## 핵심 목표

- 판매자 데이터를 내부 모델로 정규화
- 일별 지표 집계와 이상 징후 탐지
- 규칙 기반 인사이트 및 추천 액션 생성
- 운영 가능한 배치/로그/메트릭 구조 구축
- 외부 API 연동 가능한 구조를 유지하되, 현재는 CSV/Mock 입력 중심으로 개발

## 현재 개발 방향

현재 프로젝트는 아래 흐름을 중심으로 확장한다.

1. 입력 계층
   - CSV 업로드
   - 샘플 데이터 시드
   - Mock 어댑터
2. 정규화 계층
   - `seller`
   - `product`
   - `order`
   - `order_item`
   - `import_job`
3. 분석 계층
   - `daily_metric`
   - 규칙 기반 인사이트 엔진
   - 추천 액션 생성
4. 운영 계층
   - Flyway 마이그레이션
   - Prometheus / Grafana / Loki / Alloy
   - 공통 예외 처리 및 로그 추적

## 기술 스택

- Java 17
- Spring Boot 3.5
- Spring Web / Validation / Data JPA / Actuator
- PostgreSQL
- Flyway
- Swagger(OpenAPI)
- Prometheus / Grafana / Loki / Alloy
- Docker Compose

## 현재 구현 범위

- 환경별 application 설정 분리
- Docker Compose 기반 PostgreSQL 실행 환경
- JPA Auditing / BaseEntity / Flyway
- 공통 응답 포맷 및 글로벌 예외 처리
- Health Check API
- Seller / SellerCredential 예제 도메인
- 주문 CSV 적재 및 import job 상태 추적
- 일별 지표 집계, 규칙 기반 인사이트 생성, 파이프라인 실행 이력 저장
- 판매자 대시보드 조회 API
- 로컬 시연용 샘플 데이터 시드 API
- Swagger 문서화
- 메트릭 및 로그 수집 기반 관측 스택
- Grafana 기본 대시보드

## 실행 방법

### 1. 환경 변수 준비

`.env.example`를 참고해 `.env` 파일을 만든다.

### 2. DB 실행

```bash
docker compose up -d
```

### 3. 관측 스택 실행

```bash
docker compose -f docker-compose.observability.yml up -d
```

### 4. 애플리케이션 실행

```bash
./gradlew bootRun
```

### 5. 확인 경로

- API 문서: `http://localhost:8080/swagger-ui.html`
- Health Check: `http://localhost:8080/api/v1/health`
- Actuator Health: `http://localhost:8080/actuator/health`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`

## 테스트

```bash
./gradlew test
```

## 데모 실행 흐름

```bash
docker compose up -d
./gradlew bootRun
```

```bash
curl -X POST "http://localhost:8080/api/v1/admin/sample-data/bootstrap?scenario=default"
```

응답의 `targetMetricDate`, `sellerId`를 확인한 뒤:

```bash
curl -X POST "http://localhost:8080/api/v1/admin/pipelines/daily?date={previousMetricDate}"
curl -X POST "http://localhost:8080/api/v1/admin/pipelines/daily?date={targetMetricDate}"
curl "http://localhost:8080/api/v1/sellers/{sellerId}/dashboard"
curl "http://localhost:8080/api/v1/admin/pipelines/daily/runs?limit=5"
```

`ORDER_DROP` 규칙까지 보려면 `previousMetricDate`를 먼저 집계한 뒤 `targetMetricDate`를 실행해야 합니다.

## 문서

- 프로젝트 방향 전환 문서: [docs/interview/project-pivot.md](docs/interview/project-pivot.md)

## 이 프로젝트에서 보여주고 싶은 것

이 프로젝트의 목적은 특정 플랫폼의 실연동 데모보다 아래 역량을 증명하는 데 있다.

- 외부 연동을 고려한 백엔드 아키텍처 설계
- 데이터 정규화 및 저장 모델링
- 집계/배치/규칙 엔진 구현
- 운영성, 관측성, 장애 대응 구조
- 제약이 생겼을 때 목표를 재정의하고 구조를 재설계하는 판단 능력
