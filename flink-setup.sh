#!/bin/bash

cd ~
wget https://repo1.maven.org/maven2/org/apache/flink/flink-connector-jdbc/1.15.4/flink-connector-jdbc-1.15.4.jar
wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
wget https://repo1.maven.org/maven2/org/apache/flink/flink-connector-kafka/1.15.4/flink-connector-kafka-1.15.4.jar
sudo cp ~/*-*.jar /usr/lib/flink/lib/

touch flink.properties
echo "bootstrap.servers = localhost:9092" >> flink.properties 
echo "airports.uri = sciezka_do_pliku csv" >> flink.properties
echo "mysql.url = jdbc:mysql://ip_maszyny:6033/flights" >> flink.properties
echo "mysql.username = streamuser" >> flink.properties
echo "mysql.password = stream" >> flink.properties
echo "delay = A" >> flink.properties

mkdir -p src/main/resources/
mv flink.properties src/main/resources/
