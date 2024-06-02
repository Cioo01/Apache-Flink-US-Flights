#!/bin/bash

source ./env-setup.sh

CLUSTER_NAME=$(/usr/share/google/get_metadata_value attributes/dataproc-cluster-name)
kafka-console-consumer.sh \
 --bootstrap-server ${CLUSTER_NAME}-w-0:9092 --group ${GROUP_NAME}\
 --topic ${TOPIC_NAME} --from-beginning
