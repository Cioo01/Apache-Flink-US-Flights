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
export INPUT_DIR="stream-data" # zmien nazwe folderu z danymi strumieniowymi
```
### Stworz folder na dane MySQL
```sh
mkdir /tmp/datadir
```

### Uruchom kontener z instalacja bazy danych MySQL ponizszym poleceniem
```sh
docker run --name mymysql -v /tmp/datadir:/var/lib/mysql -p 6033:3306 \
 -e MYSQL_ROOT_PASSWORD=flink -d mysql:debian
```

### Wejdz do dockera z baza danych

```sh
docker exec -it mymysql bash
```

### Zaloguj sie do bazy

```sh
mysql -uroot -pflink
```

### Utworz uzytkownika bazy danych

```sql
CREATE USER 'streamuser'@'%' IDENTIFIED BY 'stream';
CREATE DATABASE IF NOT EXISTS flights CHARACTER SET utf8;
GRANT ALL ON flights.* TO 'streamuser'@'%';
```

### Wyjdz z mysql poleceniem exit

### Zaloguj sie na nowoutworzonego uzytkownika do bazy flights
```sh
mysql -u streamuser -pstream flights
```

### Stworz tabele do przechowywania agregatow
```sql
create table us_flights_sink
(
    us_state                varchar(2),
    total_departures        bigint,
    total_departures_delay  bigint,
    total_arrivals          bigint,
    total_arrivals_delay    bigint
);
```
### Wyjdz z mysql poleceniem exit

### Otworz nowy terminal (zebatka -> New connection/Nowe polaczenie) i nadaj prawo do wykonywania plikom .sh
```sh
chmod +x *.sh
```

### Po wykonaniu powyższych kroków, uruchom skrypt main (wszelkie ostrzezenia o opoznieniu mozesz spokojnie pominac)
```sh
./main.sh
```
### Przejdz do folderu src/main/resources, zostal tam stworzony plik flink.properties, w ktorym okreslisz paramtery programu.
```sh
cd src/main/resources
hostname -I # sprawdz IP maszyny, skopiuj pierwszy z lewej
nano flink.resources
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

### Zaloguj sie do bazy
```sh
mysql -u streamuser -pstream flights
```

### Wykonaj polecenie aby zobaczyc wyniki - polecam uzyc limit <liczba> na koncu
```sql
select * from us_flights_sink;
```

### Jezeli z jakiegos powodu chcialbys zaczac od poczatku, wywolaj nastepujacy skrypt
```sh
./cleanup.sh
```