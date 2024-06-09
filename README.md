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
### Wgraj jara na klaster

### Ustaw zmienne w pliku env-setup.sh
```sh
export BUCKET_NAME="placeholder" # <- Zmień na nazwę swojego bucketa
export STREAM_DIR_DATA="gs://$BUCKET_NAME/nazwa_folderu" # <- dostosuj sciezki do folderu, w ktorym przechowujesz dane strumieniowe
export STATIC_DATA="gs://$BUCKET_NAME/nazwa_pliku.csv" # <- wprowadz nazwe pliku, ktory zawiera dane statyczne
export INPUT_DIR="stream-data" # <- zmien nazwe folderu z danymi strumieniowymi
```

### Otworz nowy terminal (zebatka -> New connection/Nowe polaczenie) i nadaj prawo do wykonywania plikom .sh
```sh
chmod +x *.sh
```

### Po wykonaniu powyższych kroków, uruchom skrypt main (wszelkie ostrzezenia o opoznieniu mozesz spokojnie pominac)
```sh
./main.sh
```
### Otworz plik flink.properties, w ktorym okreslisz paramtery programu.
```sh
hostname -I # sprawdz IP maszyny, skopiuj pierwszy z lewej
nano  src/main/resources/flink.properties
```

### Parametry, ktore nalezy uzupelnic:
```
airports.uri = sciezka do pliku csv
mysql.url = jdbc:mysql://IP_MASZYNY:6033/flights
```

### Parametry, ktore badaja dzialanie programu
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

### Otworz nowy terminal i uruchom skrypt uruchamiajacy flinkowego konsumenta
```sh
./consumer.sh
```
### W nowym terminalu po chwili, wyswietl wynik agregacji

```sh
./sql-show-result.sh
```

### Jezeli z jakiegos powodu chcialbys zaczac od poczatku, wywolaj nastepujacy skrypt
```sh
./cleanup.sh
```