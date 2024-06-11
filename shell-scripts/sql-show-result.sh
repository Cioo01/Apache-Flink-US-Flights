#!/bin/bash
docker exec -it mymysql mysql -ustreamuser -pstream flights -e "SELECT * FROM us_flights_sink;"
