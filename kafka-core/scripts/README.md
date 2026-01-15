kafka 실습을 하기위해 카프카 브로커를 먼저 띄어야하기때문에 아래와 같이 파일을 리눅스에 위치한다.

### single broker 작업 시
1. confluent/etc/kafka 에 "custom.server.properties" 파일을 복사 붙혀 놓는다.
2. start-kafka.sh를 /home/계정/ 바로 밑에 복사 붙혀 놓는다.

### multi broker 작업 시

아래 3개 설정 파일 수정 해줘야함

 - custom-server-01.properties
 - custom-server-02.properties
 - custom-server-03.properties

예를들면 <your username>, <your IP> 이런것들을 본인들 작업 할수 있는 username, IP 2개를 변경 해서 사용해야한다.
```yml
log.dirs=/home/<your username>/data/kraft-combined-logs-1
controller.quorum.voters=1@<your IP>:9093,2@<your IP>:9095,3@<your IP>:9097
advertised.listeners=PLAINTEXT://<your IP>:9092,CONTROLLER://<your IP>:9093
```
