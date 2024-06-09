# Apache-Flink-US-Flights

### Uruchom klaster poniższą komendą:
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
### Wgraj zip na klaster i przejdź do głównego folderu
```sh
cd ~/
unzip project2.zip
```

### Ustaw zmienne w pliku env-setup.sh
```sh
export BUCKET_NAME="placeholder" # <- Zmień na nazwę swojego bucketa
export STREAM_DIR_DATA="gs://$BUCKET_NAME/nazwa_folderu" # <- dostosuj sciezki do folderu, w ktorym przechowujesz dane strumieniowe
export STATIC_DATA="gs://$BUCKET_NAME/nazwa_pliku.csv" # <- wprowadz nazwe pliku, ktory zawiera dane statyczne
export INPUT_DIR="stream-data" # <- zmien nazwe folderu z danymi strumieniowymi
```

### Otwórz nowy terminal (zębatka -> New connection/Nowe połączenie) i nadaj prawo do wykonywania plikom .sh
```sh
chmod +x *.sh
```

### Po wykonaniu powyższych kroków, uruchom skrypt main (wszelkie ostrzeżenia o opóźnieniu możesz spokojnie pominąć).
```sh
./main.sh
```
### Otwórz plik flink.properties, w którym określisz paramtery programu.
```sh
hostname -I # sprawdź IP maszyny, skopiuj pierwszy z lewej
nano  ~/src/main/resources/flink.properties
```

### Parametry, ktore należy uzupełnić:
```
airports.uri = sciezka do pliku csv
mysql.url = jdbc:mysql://IP_MASZYNY:6033/flights
```

### Parametry, ktore badają działanie programu
```
delay = A
D = 60
N = 30
```

### Uruchom skrypt producenta
```sh
cd ~
./producer.sh
```

### Otwórz nowy terminal i uruchom skrypt uruchamiający flinkowego konsumenta
```sh
./consumer.sh
```
### W nowym terminalu po chwili, wyświetl wynik agregacji

```sh
./sql-show-result.sh
```

### Uruchom konsumenta anomalii
```sh
./anomaly-consumer.sh
```

### Jezeli z jakiegoś powodu chciałbyś zacząć od początku, wywołaj następujący skrypt (!UWAGA! - skrypt usuwa wszystko)
```sh
./cleanup.sh
```

## Transformacje - obraz czasu rzeczywistego
```java
.filter(array -> array.length == 25)
.filter(array -> !array[0].startsWith("airline"))
.filter(array -> DateUtils.parseDateTime(array[23], new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), array[24], "orderColumn") != null)
```
Filtrowanie wyników dla rekordów, które są za krótkie, pomijam headery i sprawdzam czy kolumna, według której nadawany jest watermark jest nullem czy nie.

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
Obliczany agregat na poziomie stanu i dnia, sume opóźnień dla przylotów i odlotów. Służy do tego funkcja getDelay, która sumuje wartości i weryfikuje czy wynik jest większy od zera, a następnie dodaje do sumy. Stan uzyskiwany jest z pliku statycznego, który wczytywany jest do mapy.

## Utrzymanie obrazu czasu rzeczywistego tryb A i C
W projekcie zaimplementowano własny Trigger, który w zależności od trybu wyzwala wyniki w określony sposób.
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
W całej klasie CustomTrigger sprawdzany jest tryb, w jakim ma działać aplikacja i w zależności od niego, dostosowywana jest emisja wyników. Powyższy przykład prezentuje zasadę implementacji pozostałych funkcji.

## Wykrywanie anomalii

```java
DataStream<FlightAnomalyData> anomalyDataStream = flightDataDS
        .keyBy(FlightData::getDestAirport)
        .window(SlidingEventTimeWindows.of(Time.minutes(D), Time.minutes(10)))
        .process(new AnomalyDetectionProcessFunction(airports, N));
```

Kluczowanie następuje po lotnisku, a następnie oknem przesuwnym sprawdzamy ile samolotów zmierza w kierunku lotniska docelowego. Funkcja AnomalyDetectionProcessFunction bierze za parametr mapę z lotniskami oraz liczbę samolotów, które mają dolecieć do nas w ciągu 30 minut. W funkcji wykonywane jest filtrowanie oraz sumowanie zdarzeń. Fragment logiki znajduje się poniżej

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

## Program przetwarzający strumienie danych; skrypt uruchamiający
Skrypt uruchamiający program to skrypt consumer.sh, natomiast wszystkie jego parametry można wyedytować w pliku flink.properties (zgodnie z instrukcją wyżej).

## Miejsce utrzymywania obrazów czasu rzeczywistego – skrypt tworzący
Skrypt tworzący miejsce utrzymywania czasu rzeczywistego jest zawarty w skrypcie mysql-setup.sh, natomiast jest on uruchamiany w wywołaniu skryptu main.sh.

## Miejsce utrzymywania obrazów czasu rzeczywistego – cechy
Zdecydowałem się użyć bazy MySQL, ponieważ zapewnia ona:
- integrowalność - zapewnia łatwą integrację i obsługę,
- wydajność - oferuje dobre wsparcie dla dużych ilości danych i wysokiej wydajności zapytań,
- stabilność i niezawodność

## Konsument: skrypt odczytujący wyniki przetwarzania
Skrypt do odczytu obrazu rzeczywistego:
```sh
consumer.sh
```
Skrypt do odczytu anomalii:
```sh
anomaly-consumer.sh
```
