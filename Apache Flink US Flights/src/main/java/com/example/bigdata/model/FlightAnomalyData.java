package com.example.bigdata.model;

public class FlightAnomalyData {
    private String windowStart;
    private String windowEnd;
    private String airportName;
    private String iata;
    private String city;
    private String state;
    private Long totalArrivingPlanes;
    private Long totalHeadingPlanes;

    public FlightAnomalyData() {
    }
    public FlightAnomalyData(String windowStart, String windowEnd, String airportName, String iata, String city, String state, Long totalArrivingPlanes, Long totalHeadingPlanes) {
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.airportName = airportName;
        this.iata = iata;
        this.city = city;
        this.state = state;
        this.totalArrivingPlanes = totalArrivingPlanes;
        this.totalHeadingPlanes = totalHeadingPlanes;
    }
    public String getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(String windowStart) {
        this.windowStart = windowStart;
    }

    public String getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(String windowEnd) {
        this.windowEnd = windowEnd;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getTotalArrivingPlanes() {
        return totalArrivingPlanes;
    }

    public void setTotalArrivingPlanes(Long totalArrivingPlanes) {
        this.totalArrivingPlanes = totalArrivingPlanes;
    }

    public Long getTotalHeadingPlanes() {
        return totalHeadingPlanes;
    }

    public void setTotalHeadingPlanes(Long totalHeadingPlanes) {
        this.totalHeadingPlanes = totalHeadingPlanes;
    }

    @Override
    public String toString() {
        return "FlightAnomalyData{" +
                "windowStart='" + windowStart + '\'' +
                ", windowEnd='" + windowEnd + '\'' +
                ", airportName='" + airportName + '\'' +
                ", iata='" + iata + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", totalArrivingPlanes=" + totalArrivingPlanes +
                ", totalHeadingPlanes=" + totalHeadingPlanes +
                '}';
    }

}
