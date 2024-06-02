# Run the cluster with the following command:

```sh
gcloud dataproc clusters create ${CLUSTER_NAME} \
--enable-component-gateway --region ${REGION} --subnet default \
--master-machine-type n1-standard-4 --master-boot-disk-size 50 \
--num-workers 2 --worker-machine-type n1-standard-2 --worker-boot-disk-size 50 \
--image-version 2.1-debian11 --optional-components DOCKER,ZOOKEEPER \
--project ${PROJECT_ID} --max-age=3h \
--metadata "run-on-master=true" \
--initialization-actions \
gs://goog-dataproc-initialization-actions-${REGION}/kafka/kafka.sh
```

## Edit the environment variables in the env-setup.sh file
```sh
export BUCKET_NAME="placeholder" # <- Change this to your bucket name
export STREAM_DIR_DATA="gs://$BUCKET_NAME/bd-stream-project2/stream-data" # <- Change this to your location
export STATIC_DATA="gs://$BUCKET_NAME/bd-stream-project2/airports.csv" # <- Change this to your location
```

## After making the changes, run the following script:
```sh
./main.sh
```

## Run the producer and consumer scripts in separate terminals
```sh
./producer.sh
./consumer.sh
```

## In case you want to start over, run the following script:
```sh
./cleanup.sh
```