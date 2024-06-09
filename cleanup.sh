#!/bin/bash

source ./env-setup.sh

echo "Deleting kafka topics"
kafka-topics.sh --delete --bootstrap-server ${CLUSTER_NAME}-w-1:9092 --topic ${TOPIC_NAME}
kafka-topics.sh --delete --bootstrap-server ${CLUSTER_NAME}-w-1:9092 --topic ${ANOMALY_TOPIC_NAME}
echo "Kafka topics deleted"