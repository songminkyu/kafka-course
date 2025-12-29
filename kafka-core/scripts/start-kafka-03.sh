#!/bin/bash

# Kafka 시작 스크립트
# zookeeper 기반이 아닌 kraft 방식

echo "Kafka 서버 3 시작 중..."
export JMX_PORT=9999
kafka-server-start $CONFLUENT_HOME/etc/kafka/custom-server-03.properties
