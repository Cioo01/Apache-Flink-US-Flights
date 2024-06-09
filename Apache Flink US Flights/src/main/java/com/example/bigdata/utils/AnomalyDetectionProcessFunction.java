package com.example.bigdata.utils;

import com.example.bigdata.model.AirportData;
import com.example.bigdata.model.FlightAnomalyData;
import com.example.bigdata.model.FlightData;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Map;

public class AnomalyDetectionProcessFunction extends ProcessWindowFunction<FlightData, FlightAnomalyData, String, TimeWindow> {

    private final Map<String, AirportData> airports;
    private final int threshold;

    public AnomalyDetectionProcessFunction(Map<String, AirportData> airports, int threshold) {
        this.airports = airports;
        this.threshold = threshold;
    }

    @Override
    public void process(String airport, Context context, Iterable<FlightData> elements, Collector<FlightAnomalyData> out) throws Exception {
        long arrivingPlanes = 0;
        long headingPlanes = 0;
        long windowStartWithDelay = context.window().getStart() + 1800000;

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

        if (arrivingPlanes >= threshold) {
            AirportData airportData = airports.get(airport);
            FlightAnomalyData anomalyData = new FlightAnomalyData();
            anomalyData.setWindowStart(String.valueOf(context.window().getStart()));
            anomalyData.setWindowEnd(String.valueOf(context.window().getEnd()));
            anomalyData.setAirportName(airportData.getName());
            anomalyData.setIata(airportData.getIata());
            anomalyData.setCity(airportData.getCity());
            anomalyData.setState(airportData.getState());
            anomalyData.setTotalArrivingPlanes(arrivingPlanes);
            anomalyData.setTotalHeadingPlanes(headingPlanes + arrivingPlanes);
            out.collect(anomalyData);
        }
    }

}