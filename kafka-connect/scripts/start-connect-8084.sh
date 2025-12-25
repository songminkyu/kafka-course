#!/bin/bash

# Kafka-connect-8084 시작 스크립트

echo "Kafka connect-8084 서버를 시작합니다..."

# 1. Kafka connect 서버 시작
echo "connect 8084 서버 시작 중..."
connect-distributed $CONFLUENT_HOME/etc/kafka/connect-distributed-8084.properties
