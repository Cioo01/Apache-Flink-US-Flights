#!/bin/bash

export CLUSTER_NAME=$(/usr/share/google/get_metadata_value attributes/dataproc-cluster-name)
export BUCKET_NAME="placeholder" # <- Change this to your bucket name
export TOPIC_NAME="flights-in-us"
export GROUP_NAME="flights-group"
export INPUT_DIR="stream-data"
export SLEEP_TIME="10"
export HEADER_LENGTH="1"
export STREAM_DIR_DATA="gs://$BUCKET_NAME/bd-stream-project2/stream-data" # <- Change this to your location
export STATIC_DATA="gs://$BUCKET_NAME/bd-stream-project2/airports.csv" # <- Change this to your location

