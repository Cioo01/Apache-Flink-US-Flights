# Apache-Flink-US-Flights

### Start the cluster with the following command:
```sh
gcloud dataproc clusters create ${CLUSTER_NAME} \
--enable-component-gateway --region ${REGION} --subnet default \
--master-machine-type n1-standard-4 --master-boot-disk-size 50 \
--num-workers 2 --worker-machine-type n1-standard-2 --worker-boot-disk-size 50 \
--image-version 2.1-debian11 --optional-components DOCKER,ZOOKEEPER,FLINK \
--project ${PROJECT_ID} --max-age=3h \
--metadata "run-on-master=true" \
--initialization-actions \
gs://goog-dataproc-initialization-actions-${REGION}/kafka/kafka.sh
```
### Upload the zip to the cluster and navigate to the main folder
```sh
cd ~/
unzip project2.zip
```

### Ustaw zmienne w pliku env-setup.sh
```sh
export BUCKET_NAME="placeholder" # <- Change to the name of your bucket
export STREAM_DIR_DATA="gs://$BUCKET_NAME/nazwa_folderu" # <- Adjust paths to the folder where you store the file simulating the stream data
export STATIC_DATA="gs://$BUCKET_NAME/nazwa_pliku.csv" # <- Enter the name of the file that contains the static data
export INPUT_DIR="stream-data" # <- Change the name of the folder with the stream data
```

### Open a new terminal (gear icon -> New connection) and grant execution rights to the .sh files
```sh
chmod +x *.sh
```

### After completing the above steps, run the main script (you can ignore any delay warnings).
```sh
./main.sh
```
### Open the flink.properties file to set the program parameters.
```sh
hostname -I # check the machine's IP, copy the first one on the left
nano  ~/src/main/resources/flink.properties
```

### Parameters to fill in:
```
airports.uri = path_to_the_csv_file
mysql.url = jdbc:mysql://MACHINE_IP:6033/flights
```

### Parameters to test the program:
```
delay = A
D = 60
N = 30
```

### Run the producer script
```sh
cd ~
./producer.sh
```

### Otwórz nowy terminal i uruchom skrypt uruchamiający flinkowego konsumenta
```sh
./consumer.sh
```
### In a new terminal, after a moment, display the aggregation result

```sh
./sql-show-result.sh
```

### Start the anomaly consumer
```sh
./anomaly-consumer.sh
```

### If for any reason you want to start from scratch, run the following script (WARNING! - the script deletes everything)
```sh
./cleanup.sh
```

## Transformations - Real-time image
```java
.filter(array -> array.length == 25)
.filter(array -> !array[0].startsWith("airline"))
.filter(array -> DateUtils.parseDateTime(array[23], new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), array[24], "orderColumn") != null)
```
Filtering results for records that are too short, skipping headers, and checking if the column used to set the watermark is null or not.

```java
Map<String, AirportData> airports = AirportUtils.getAirportsFromFile(airportPath);
.
.
.
DataStream<FlightDataAgg> flightDataAggDS = flightDataDS
        .map(fd -> {
            String state = airports.get(fd.getStartAirport()).getState();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String day = dateFormat.format(fd.getOrderColumn());
            FlightDataAgg agg = new FlightDataAgg(state, day,0L, 0L, 0L, 0L);

            agg.addDeparture(FlightUtils.getDelay(fd.getDepartureTime(), fd.getScheduledDepartureTime()));
            agg.addArrival(FlightUtils.getDelay(fd.getArrivalTime(), fd.getScheduledArrivalTime()));

            return agg;
        })
        .keyBy((KeySelector<FlightDataAgg, String>) FlightDataAgg::getState)
        .window(new DayWindowAssigner(delay))
        .apply(new FinalWindowResult());
```
Aggregate calculation at the state and day level, sum of delays for arrivals and departures. The getDelay function is used to sum the values and check if the result is greater than zero, then adds to the total. The state is obtained from a static file that is loaded into a map.

## Maintaining real-time image mode A and C
The project implemented a custom Trigger that triggers results in a specific way depending on the mode.
```java
@Override
public TriggerResult onElement(Object element, long timestamp, TimeWindow window, TriggerContext ctx) {
    // Tryb A: Natychmiastowe wyzwalanie wyników dla najmniejszych opóźnień z aktualizacjami w czasie rzeczywistym
    if (FlinkPropertiesUtil.getDelay().equals("A")) {
        return TriggerResult.FIRE;
    } else {
        // Tryb C: Rejestracja timera zdarzeń czasowych dla maksymalnego czasu w oknie, żeby zapewnić kompletność okna
        ctx.registerEventTimeTimer(window.maxTimestamp());
        return TriggerResult.CONTINUE;
    }
}
```
The entire CustomTrigger class checks the mode the application is to run in and adjusts the emission of results accordingly. The above example demonstrates the principle of implementing other functions.

## Anomaly Detection

```java
DataStream<FlightAnomalyData> anomalyDataStream = flightDataDS
        .keyBy(FlightData::getDestAirport)
        .window(SlidingEventTimeWindows.of(Time.minutes(D), Time.minutes(10)))
        .process(new AnomalyDetectionProcessFunction(airports, N));
```

The keying is done by the airport, and then a sliding window checks how many planes are heading towards the destination airport. The AnomalyDetectionProcessFunction takes as a parameter a map of airports and the number of planes that are to arrive within 30 minutes. Filtering and summing events are performed in the function. A fragment of the logic is shown below:

```java
for (FlightData flight : elements) {
    if (flight.getScheduledArrivalTime() == null) {
        continue;
    }
    long arrivalTime = flight.getScheduledArrivalTime().getTime();

    if (arrivalTime >= windowStartWithDelay && arrivalTime <= context.window().getEnd()) {
        arrivingPlanes++;
    }

    else if (arrivalTime > context.window().getEnd()) {
        headingPlanes++;
    }
}
```

## Data Stream Processing Program; Startup Script
The script to start the program is consumer.sh, while all its parameters can be edited in the flink.properties file (as per the instructions above).

## Real-time image storage location – creation script
The script creating the real-time image storage location is included in the mysql-setup.sh script, which is executed in the main.sh script call.

## Real-time image storage location – features
MySQL was chosen as the storage database because it provides:
- Integrability - ensures easy integration and handling,
- Performance - offers good support for large data volumes and high query performance,
- Stability and reliability

## Consumer: script to read processing results
Script to read the real-time image:
```sh
consumer.sh
```
Script to read anomalies:
```sh
anomaly-consumer.sh
```
