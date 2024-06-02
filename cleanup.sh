#!/bin/bash

source ./env-setup.sh

echo "Deleting kafka topic"
kafka-topics.sh --delete --bootstrap-server ${CLUSTER_NAME}-w-1:9092 --topic ${TOPIC_NAME}
echo "Kafka topic deleted"