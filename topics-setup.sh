#!/bin/bash

source ./env-setup.sh

echo "Creating kafka topic"
kafka-topics.sh --create --bootstrap-server ${CLUSTER_NAME}-w-1:9092 --replication-factor 1 --partitions 1 --topic ${TOPIC_NAME}
echo "Kafka topic created"

echo "Listing kafka topics"

kafka-topics.sh --bootstrap-server ${CLUSTER_NAME}-w-1:9092 --list
