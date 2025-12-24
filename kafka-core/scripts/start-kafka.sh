#!/bin/bash

# Kafka 시작 스크립트
# zookeeper 기반이 아닌 kraft 방식

echo "Kafka 서버를 시작합니다..."

# 1. UUID 생성
echo "클러스터 UUID 생성 중..."
CLUSTER_UUID=$(kafka-storage random-uuid)
echo "생성된 UUID: $CLUSTER_UUID"

# 2. 스토리지 포맷
echo "스토리지 포맷 중..."
kafka-storage format -t $CLUSTER_UUID -c $CONFLUENT_HOME/etc/kafka/custom.server.properties --standalone

# 3. Kafka 서버 시작
echo "Kafka 서버 시작 중..."
kafka-server-start $CONFLUENT_HOME/etc/kafka/custom.server.properties
