#!/bin/bash

flink run -m yarn-cluster -p 4 \
 -yjm 1024m -ytm 1024m -c \
 com.example.bigdata.FlightsAnalysis ~/FlightsAnalysis.jar
