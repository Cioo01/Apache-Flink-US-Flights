#!/bin/bash

flink run -m yarn-cluster -p 4 \
 -yjm 2048m -ytm 2048m -c \
 com.example.bigdata.FlightsAnalysis ~/FlightsAnalysis.jar
