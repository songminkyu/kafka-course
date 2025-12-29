# Kafka Core Complete Guide (카프카 핵심 완벽 가이드) - 실습 코드

본 프로젝트는 "카프카 핵심 완벽 가이드" 강의의 실습 코드를 포함하고 있습니다. Kafka Client(Producer, Consumer)의 기초부터 고급 활용, 그리고 실제 데이터 파이프라인 구축 시나리오까지 다양한 예제를 다룹니다.

## 📁 프로젝트 구조 (Project Structure)

프로젝트는 크게 3개의 모듈로 구성되어 있습니다.

### 1. Producers (`producers`)
Kafka Producer의 다양한 전송 방식과 설정을 학습합니다.
- **SimpleProducer**: 가장 기본적인 "Fire-and-Forget" 방식의 프로듀서
- **SimpleProducerSync / Async**: 동기(Sync) 및 비동기(Async) 전송 방식
- **ProducerASyncWithKey**: Key를 지정하여 순서를 보장하는 비동기 전송
- **PizzaProducer**: 피자 주문 시나리오를 가정한 모의 데이터 생성기 (Load Test용)
- **CustomPartitioner**: 사용자 정의 파티셔너 구현 예제

### 2. Consumers (`consumers`)
Kafka Consumer의 그룹 관리, 오프셋 커밋, 멀티 스레드 처리를 학습합니다.
- **SimpleConsumer**: 기본적인 컨슈머 구현
- **ConsumerWakeup**: `wakeup()`을 이용한 우아한 종료(Graceful Shutdown) 처리
- **ConsumerCommit**: 수동 오프셋 커밋(Sync/Async) 관리
- **ConsumerPartitionAssign**: 특정 파티션 직접 할당
- **Multi-threaded Consumers**:
    - `MultithreadedConsumerPerThread`: 스레드별 컨슈머 할당 패턴
    - `DecoupledConsumerWorkerPool`: Fetcher와 Worker를 분리한 패턴

### 3. Practice (`practice`)
실전 데이터 파이프라인 및 응용 시나리오를 다룹니다.
- **File-to-DB Pipeline**: 로그 파일을 읽어 Kafka로 전송하고, DB에 적재하는 파이프라인
    - `FileProducer`: 로그 파일 생성 및 전송
    - `FileToDBConsumer`: 데이터를 소비하여 데이터베이스(PostgreSQL)에 적재 (JDBC 사용)
- **Order System**: 주문 데이터(`OrderDTO`)의 직렬화/역직렬화 및 이벤트 처리

---

## 🚀 실행 환경 설정 (Environment Setup)

이 프로젝트는 Docker Compose를 사용하여 Kafka 브로커와 PostgreSQL 데이터베이스를 실행합니다.

### 사전 요구 사항
- Java 17+ (권장)
- Docker & Docker Compose
- IntelliJ IDEA (권장)

### 서비스 실행
프로젝트 루트 디렉토리에서 아래 명령어를 실행하여 Kafka와 DB를 실행합니다.

```bash
# 기존 컨테이너 및 볼륨 삭제 (초기화)
docker compose down -v

# 서비스 백그라운드 실행
docker compose up -d
```

> **Note**: `postgres_init/init.sql`이 실행되지 않는 경우, 반드시 `-v` 옵션을 사용하여 기존 볼륨을 삭제하고 다시 실행해야 합니다.

---

## 📖 주요 예제 실행 방법

각 예제는 `main()` 메서드를 포함한 독립적인 Java 클래스로 구성되어 있습니다. IDE에서 해당 파일을 열고 실행(Run)하면 됩니다.

1. **Docker 실행**: 위 명령어로 Kafka 환경을 먼저 구성합니다.
2. **토픽 생성**: (선택 사항) 필요한 경우 스크립트나 CLI를 통해 토픽을 미리 생성합니다.
3. **Producer 실행**: `producers` 모듈의 예제를 실행하여 메시지를 발행합니다. (예: `SimpleProducer`)
4. **Consumer 실행**: `consumers` 모듈의 예제를 실행하여 메시지를 수신합니다. (예: `SimpleConsumer`)

---

## 🛠 기술 스택
- **Apache Kafka Clients**: 4.1.1
- **SLF4J**: 2.0.17
- **PostgreSQL**: 15 (Docker)
- **Java**: 17
