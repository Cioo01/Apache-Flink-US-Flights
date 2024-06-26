#!/bin/bash

echo "Setting up environment variables"
source ./env-setup.sh
echo "Environment variables set up"

echo "HDFS setup"
hadoop fs -copyToLocal $STREAM_DIR_DATA
hadoop fs -copyToLocal $STATIC_DATA
echo "HDFS setup complete"

echo "Setting up MySQL"
./mysql-setup.sh
echo "MySQL setup complete"

echo "Setting up Kafka topics"
./topics-setup.sh
echo "Kafka topics set up"

echo "Setting up Flink"
./flink-setup.sh
echo "Flink setup almost complete"
echo "Przejdz do edycji pliku flink.properties"