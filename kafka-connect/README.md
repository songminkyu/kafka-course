# "카프카 완벽 가이드 - Connect 편" 강의 실습코드

이 프로젝트는 Kafka Connect의 이해와 실습을 돕기 위해 구성된 코드 저장소입니다.
JDBC Connector와 SpoolDir Connector 등을 활용한 다양한 실습 예제와 편의 스크립트를 포함하고 있습니다.

## 📂 디렉토리 구조

- **connector_configs/**: Kafka Connector 등록을 위한 JSON 설정 파일 모음
  - JDBC Source/Sink Connector 설정
  - SpoolDir Source Connector 설정
- **scripts/**: Kafka 환경 실행 및 Connector 관리를 위한 쉘 스크립트 모음
  - Zookeeper, Kafka Broker, Kafka Connect 실행 스크립트
  - Connector 등록(`register_connector`), 삭제(`delete_connector`), 조회(`show_connectors`) 스크립트
- **sample_data/**: File Connector 실습을 위한 샘플 데이터
- **실습수행/**: 실습 과정 기록 및 참고 자료

## 🚀 시작하기

### 1. 환경 준비
`scripts` 디렉토리에 있는 스크립트를 사용하여 실습 환경을 구동할 수 있습니다.

```bash
# Zookeeper 실행
./scripts/zoo_start.sh

# Kafka 실행
./scripts/kafka_start.sh

# Kafka Connect 실행 (분산 모드)
./scripts/start-connect-8083.sh
```

### 2. Connector 관리
제공되는 편의 스크립트를 통해 Connector를 쉽게 관리할 수 있습니다.

```bash
# Connector 등록
./scripts/register_connector <config-file-path>
# 예: ./scripts/register_connector connector_configs/mysql_jdbc_om_source.json

# 등록된 Connector 목록 조회
./scripts/show_connectors

# Connector 삭제
./scripts/delete_connector <connector-name>
```

## 📝 주요 실습 내용
- **JDBC Source Connector**: DB 테이블의 데이터를 Kafka Topic으로 수집 (Bulk, Timestamp+Increment 모드 등)
- **JDBC Sink Connector**: Kafka Topic의 데이터를 DB 테이블로 적재
- **SpoolDir Connector**: CSV 등 특정 디렉토리의 파일을 감시하여 데이터 수집

---
> 본 코드는 강의 실습용으로 작성되었습니다.
