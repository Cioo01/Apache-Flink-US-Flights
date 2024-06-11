#!/bin/bash

mkdir -p /tmp/datadir

docker run --name mymysql -v /tmp/datadir:/var/lib/mysql -p 6033:3306 \
 -e MYSQL_ROOT_PASSWORD=flink -d mysql:debian


echo "Waiting for MySQL server to start..."
sleep 5
echo "."
sleep 5
echo "."
sleep 5
echo "."
sleep 5
echo "."

docker exec -i mymysql mysql -uroot -pflink <<EOF
CREATE USER 'streamuser'@'%' IDENTIFIED BY 'stream';
CREATE DATABASE IF NOT EXISTS flights CHARACTER SET utf8;
GRANT ALL ON flights.* TO 'streamuser'@'%';
EOF

docker exec -i mymysql mysql -ustreamuser -pstream flights <<EOF
CREATE TABLE us_flights_sink (
    us_state                VARCHAR(2),
    day                     DATE,
    total_departures        BIGINT,
    total_departures_delay  BIGINT,
    total_arrivals          BIGINT,
    total_arrivals_delay    BIGINT
);
EOF
