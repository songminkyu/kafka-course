#!/bin/bash

# Multi-Broker Cluster Initialization Script

echo "1. Generating unique Cluster UUID..."
CLUSTER_UUID=$(kafka-storage random-uuid)
echo "Generated UUID: $CLUSTER_UUID"

echo "1.5 Cleaning up used logs..."
rm -rf /home/smk/data/kraft-combined-logs-1 /home/smk/data/kraft-combined-logs-2 /home/smk/data/kraft-combined-logs-3

echo "2. Formatting Storage for Broker 1..."
kafka-storage format -t $CLUSTER_UUID -c $CONFLUENT_HOME/etc/kafka/custom-server-01.properties --ignore-formatted

echo "3. Formatting Storage for Broker 2..."
kafka-storage format -t $CLUSTER_UUID -c $CONFLUENT_HOME/etc/kafka/custom-server-02.properties --ignore-formatted

echo "4. Formatting Storage for Broker 3..."
kafka-storage format -t $CLUSTER_UUID -c $CONFLUENT_HOME/etc/kafka/custom-server-03.properties --ignore-formatted

echo "------------------------------------------------"
echo "Cluster initialization complete!"
echo "You can now start the brokers using:"
echo "  ./start-kafka-01.sh"
echo "  ./start-kafka-02.sh"
echo "  ./start-kafka-03.sh"
