#!/bin/bash

# Kafka-connect-8083 시작 스크립트

echo "Kafka connect-8083 서버를 시작합니다..."

# 1. Kafka connect 서버 시작
echo "connect 8083 서버 시작 중..."
connect-distributed $CONFLUENT_HOME/etc/kafka/connect-distributed.properties
