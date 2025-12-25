#!/bin/bash

# Kafka-connect-8084 시작 스크립트

echo "Kafka connect-log-8084 서버를 시작합니다..."

# 1. Kafka connect 서버 시작
echo "connect-log 8084 서버 시작 중..."
log_suffix=`date +"%Y%m%d%H%M%S"`
connect-distributed $CONFLUENT_HOME/etc/kafka/connect-distributed-8084.properties 2>&1 | tee -a ~/connect_console_log/connect_console_$log_suffix.log

