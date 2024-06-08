package com.example.bigdata.utils;
import com.example.bigdata.model.FlightDataAgg;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class FinalWindowResult implements WindowFunction<FlightDataAgg, FlightDataAgg, String, TimeWindow> {
    @Override
    public void apply(String key, TimeWindow window, Iterable<FlightDataAgg> input, Collector<FlightDataAgg> out) throws Exception {
        FlightDataAgg result = null;
        for (FlightDataAgg agg : input) {
            if (result == null) {
                result = new FlightDataAgg(agg.getState(), agg.getTotalDepartures(), agg.getTotalArrivals(), agg.getTotalDeparturesDelay(), agg.getTotalArrivalsDelay());
            } else {
                result.addDeparture(agg.getTotalDeparturesDelay());
                result.addArrival(agg.getTotalArrivalsDelay());
                result.setTotalDepartures(result.getTotalDepartures() + agg.getTotalDepartures());
                result.setTotalArrivals(result.getTotalArrivals() + agg.getTotalArrivals());
            }
        }
        if (result != null) {
            out.collect(result);
        }
    }
}

