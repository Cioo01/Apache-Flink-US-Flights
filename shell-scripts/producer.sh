#!/bin/bash

source ./env-setup.sh

java -cp /usr/lib/kafka/libs/*:KafkaProducer.jar com.example.bigdata.TestProducer \
  "$INPUT_DIR" \
  "$SLEEP_TIME" \
  "$TOPIC_NAME" \
  "$HEADER_LENGTH" \
  ${CLUSTER_NAME}-w-0:9092