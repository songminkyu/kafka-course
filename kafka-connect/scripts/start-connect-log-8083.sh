#!/bin/bash

# Kafka-connect-8083 시작 스크립트

echo "Kafka connect-log-8083 서버를 시작합니다..."

# 1. Kafka connect 서버 시작
echo "connect-log 8083 서버 시작 중..."
log_suffix=`date +"%Y%m%d%H%M%S"`
connect-distributed $CONFLUENT_HOME/etc/kafka/connect-distributed.properties 2>&1 | tee -a ~/connect_console_log/connect_console_$log_suffix.log

