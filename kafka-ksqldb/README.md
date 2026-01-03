# "카프카 완벽 가이드 - KSQLDB 편" 강의 실습 코드

이 프로젝트는 "카프카 완벽 가이드 - KSQLDB 편" 강의 학습을 위한 실습 코드와 가이드 문서를 포함하고 있습니다. KSQLDB의 핵심 개념인 Stream과 Table부터, Join, Windowing, 그리고 외부 시스템 연결(Kafka Connect, Elasticsearch)까지 폭넓은 내용을 다룹니다.

## 📂 프로젝트 구성 및 파일 설명

이 저장소는 주제별 마크다운(`*.md`) 파일로 구성되어 있으며, 각 파일은 해당 주제에 대한 이론 설명과 실습 명령어를 담고 있습니다.

### 1. ⚙️ 환경 설정 및 기초
- **[kafka와 ksqldb 환경 설정 및 기동하기.md](kafka와%20ksqldb%20환경%20설정%20및%20기동하기.md)**  
  Confluent Platform을 기반으로 Zookeeper, Kafka, Schema Registry, KSQLDB를 설치하고 구동하는 방법을 안내합니다. 가장 먼저 확인해야 할 문서입니다.
- **[KSQLDB Stream과 Table 소개.md](KSQLDB%20Stream과%20Table%20소개.md)**  
  KSQLDB의 가장 기본이 되는 구조인 Stream과 Table의 개념을 익히고, 생성(CREATE) 및 조회(SELECT)하는 기본적인 방법을 실습합니다.

### 2. 🔍 핵심 기능 이해
- **[Join 이해.md](Join%20이해.md)**  
  Stream-Stream, Table-Table, Stream-Table 등 다양한 형태의 데이터 조인(Join) 방법을 다룹니다.
- **[KSQLDB Group by와 MView 이해.md](KSQLDB%20Group%20by와%20MView%20이해.md)**  
  데이터 집계(Aggregation)를 위한 `GROUP BY` 구문과 이를 통해 생성되는 Materialized View(MView)에 대해 학습합니다.
- **[Time과 Windows 이해.md](Time과%20Windows%20이해.md)**  
  이벤트 시간(Event Time) 처리와 데이터 윈도우(Tumbling, Hopping, Session Window) 설정 방법을 다룹니다.
- **[KSQL 활용.md](KSQL%20활용.md)**  
  KSQL 문법의 다양한 활용 사례와 팁을 정리했습니다.

### 3. 🔌 외부 연동 및 확장
- **[KSQLDB와 Connect 연동 - 01.md](KSQLDB와%20Connect%20연동%20-%2001.md)**  
  KSQLDB 내부에서 Kafka Connect를 제어하고 연동하는 방법을 실습합니다.
- **[Elasticsearch Sink Connector 구성.md](Elasticsearch%20Sink%20Connector%20구성.md)**  
  KSQLDB로 처리된 데이터를 Elasticsearch로 적재하기 위한 Sink Connector 설정 방법을 다룹니다.

### 4. 💡 실전 활용
- **[KSQLDB Usecase 실전 실습.md](KSQLDB%20Usecase%20실전%20실습.md)**  
  앞서 배운 내용들을 종합하여 실제 비즈니스 시나리오와 유사한 유스케이스를 구현해봅니다.

## 🚀 학습 가이드

1. **환경 구축**: `kafka와 ksqldb 환경 설정 및 기동하기.md`를 따라 실습 환경을 준비하세요.
2. **기본기 다지기**: `KSQLDB Stream과 Table 소개.md`를 통해 데이터 추상화 개념을 확실히 이해하세요.
3. **기능별 실습**: Join, Group By, Windowing 등 관심 있는 주제의 파일을 열어 예제 코드를 직접 실행해보세요.
4. **시스템 확장**: Kafka Connect 및 Elasticsearch 연동 실습을 통해 데이터 파이프라인을 구축해보세요.

## 📦 기타 파일
- `confluentinc-kafka-connect-datagen-0.6.0.zip`: 실습용 더미 데이터를 생성하기 위한 Datagen Connector 플러그인 파일입니다.

---
*이 자료는 강의 수강생들의 실습 편의를 위해 제공됩니다.*
