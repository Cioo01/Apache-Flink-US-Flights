package com.example.bigdata.model;

import java.util.Date;

public class FlightDataAgg {
    private String state;
    private String day;
    private Long totalDepartures;
    private Long totalDeparturesDelay;
    private Long totalArrivals;
    private Long totalArrivalsDelay;

    public FlightDataAgg(String state, String day, Long totalDepartures, Long totalDeparturesDelay, Long totalArrivals, Long totalArrivalsDelay) {
        this.state = state;
        this.day = day;
        this.totalDepartures = totalDepartures;
        this.totalDeparturesDelay = totalDeparturesDelay;
        this.totalArrivals = totalArrivals;
        this.totalArrivalsDelay = totalArrivalsDelay;
    }

    public String getState() {
        return state;
    }
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
    public Long getTotalDepartures() {
        return totalDepartures;
    }

    public Long getTotalDeparturesDelay() {
        return totalDeparturesDelay;
    }

    public Long getTotalArrivals() {
        return totalArrivals;
    }

    public Long getTotalArrivalsDelay() {
        return totalArrivalsDelay;
    }


    public void addDeparture(Long delay) {
        totalDepartures++;
        if (delay > 0) {
            totalDeparturesDelay += delay;
        }
    }
    public void setTotalDepartures(Long totalDepartures) {
        this.totalDepartures = totalDepartures;
    }

    public void addArrival(Long delay) {
        totalArrivals++;
        if (delay > 0) {
            totalArrivalsDelay += delay;
        }
    }

    public void setTotalArrivals(Long totalArrivals) {
        this.totalArrivals = totalArrivals;
    }

    @Override
    public String toString() {
        return "FlightDataAgg{" +
                "state='" + state + '\'' +
                ", day='" + day + '\'' +
                ", totalDepartures=" + totalDepartures +
                ", totalDeparturesDelay=" + totalDeparturesDelay +
                ", totalArrivals=" + totalArrivals +
                ", totalArrivalsDelay=" + totalArrivalsDelay +
                '}';
    }
}
