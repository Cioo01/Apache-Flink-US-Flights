#!/bin/bash

source ./env-setup.sh
/usr/lib/kafka/bin/kafka-console-consumer.sh \
 --bootstrap-server ${CLUSTER_NAME}-w-1:9092 \
 --topic flight-anomalies --from-beginning