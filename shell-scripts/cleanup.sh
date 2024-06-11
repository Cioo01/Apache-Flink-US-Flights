#!/bin/bash

source ./env-setup.sh

echo "Deleting kafka topics"
kafka-topics.sh --delete --bootstrap-server ${CLUSTER_NAME}-w-1:9092 --topic ${TOPIC_NAME}
kafka-topics.sh --delete --bootstrap-server ${CLUSTER_NAME}-w-1:9092 --topic ${ANOMALY_TOPIC_NAME}
echo "Kafka topics deleted"

echo "Deleting docker image"
docker exec -i mymysql mysql -ustreamuser -pstream flights <<EOF
DROP TABLE IF EXISTS us_flights_sink;
EOF
docker stop mymysql && docker rm mymysql && docker rmi mysql:debian
echo "Docker image deleted"

echo "Cleaning up"
cd ~/
rm -rf ./*
echo "Clean up done"