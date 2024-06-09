#!/bin/bash

source ./env-setup.sh

echo "Creating kafka topics"
kafka-topics.sh --create --bootstrap-server ${CLUSTER_NAME}-w-1:9092 --replication-factor 1 --partitions 1 --topic ${TOPIC_NAME}
kafka-topics.sh --create --bootstrap-server ${CLUSTER_NAME}-w-1:9092 --replication-factor 1 --partitions 1 --topic ${ANOMALY_TOPIC_NAME}
echo "Kafka topics created"

echo "Listing kafka topics"

kafka-topics.sh --bootstrap-server ${CLUSTER_NAME}-w-1:9092 --list
