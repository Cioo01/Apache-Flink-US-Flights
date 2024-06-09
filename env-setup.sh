#!/bin/bash

export CLUSTER_NAME=$(/usr/share/google/get_metadata_value attributes/dataproc-cluster-name)
export BUCKET_NAME="placeholder" # <- do zmiany
export TOPIC_NAME="flights-in-us"
export ANOMALY_TOPIC_NAME="flight-anomalies"
export GROUP_NAME="flights-group"
export INPUT_DIR="stream-data" # <- do zmiany
export SLEEP_TIME="10"
export HEADER_LENGTH="1"
export STREAM_DIR_DATA="gs://$BUCKET_NAME/bd-stream-project2/stream-data" # <- do zmiany
export STATIC_DATA="gs://$BUCKET_NAME/bd-stream-project2/airports.csv" # <- do zmiany
export HADOOP_CONF_DIR=/etc/hadoop/conf
export HADOOP_CLASSPATH=`hadoop classpath`