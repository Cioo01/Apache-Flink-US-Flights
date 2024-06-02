#!/bin/bash

echo "Setting up environment variables"
source ./env-setup.sh
echo "Environment variables set up"

echo "HDFS setup"
hadoop fs -copyToLocal $STREAM_DIR_DATA
hadoop fs -copyToLocal $STATIC_DATA
echo "HDFS setup complete"

echo "Setting up Kafka topics"
./topics-setup.sh
echo "Kafka topics set up"
