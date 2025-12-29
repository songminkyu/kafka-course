# Kafka Course Practice Code

이 저장소는 **Kafka Core**, **Kafka Connect**, **Kafka KSQLDB** 등 Apache Kafka의 핵심 컴포넌트를 학습하고 실습하기 위한 코드를 포함하고 있습니다.

## 📂 프로젝트 구성 (Project Structure)

프로젝트는 크게 3가지 주요 파트로 나누어져 있습니다.

### 1. [kafka-core](./kafka-core)
Kafka의 기본 개념과 Java Client(Producer, Consumer) 활용법을 다룹니다.
- **Producers**: 기본 Producer부터 비동기 전송, 키 기반 전송, 커스텀 파티셔너 등 다양한 구현 예제
- **Consumers**: 컨슈머 그룹, 오프셋 관리, 멀티 스레드 컨슈머 구조 등 고급 패턴 학습
- **Docker Environment**: Kafka 브로커와 PostgreSQL을 실행하기 위한 `docker-compose.yml` 및 초기화 스크립트 포함

### 2. [kafka-connect](./kafka-connect)
Kafka Connect를 사용하여 외부 시스템과 데이터를 주고받는 실습을 진행합니다.
- **Connectors**: 다양한 Source/Sink 커넥터 설정 예제
- **Scripts**: 커넥터 실행 및 관리를 위한 스크립트 모음
- **Practice**: 실제 데이터 연동 실습

### 3. [kafka-ksqldb](./kafka-ksqldb)
Stream Processing을 위한 KSQLDB 학습 자료입니다.
- **Concepts**: Join, Windowing, Table vs Stream 등 핵심 개념 설명 (Markdown)
- **Hands-on**: KSQLDB를 활용한 실시간 스트림 처리 실습 시나리오

---

## 🚀 시작하기 (Getting Started)

각 폴더 내의 `README.md` (있는 경우)를 참조하여 세부적인 실행 방법과 실습 내용을 확인할 수 있습니다.

### 사전 요구 사항
- Java 17+
- Docker & Docker Compose
