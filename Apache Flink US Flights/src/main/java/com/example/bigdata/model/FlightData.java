package com.example.bigdata.model;

import java.io.Serializable;
import java.util.Date;
public class FlightData {

    private String airline;
    private Integer flightNumber;
//    private String tailNumber;
    private String startAirport;
    private String destAirport;
    private Date scheduledDepartureTime;
//    private Integer scheduledDepartureDayOfWeek;
//    private Integer scheduledFlightTime;
    private Date scheduledArrivalTime;
    private Date departureTime; //real departure time
    //    private Integer taxiOut;
//    private Integer distance;
//    private Integer taxiIn;
    private Date arrivalTime;
    private boolean diverted;
    private boolean cancelled;
    //    private String cancellationReason;
//    private Integer airSystemDelay;
//    private Integer securityDelay;
//    private Integer airlineDelay;
//    private Integer lateAircraftDelay;
//    private Integer weatherDelay;
    private Date cancellationTime;
    private Date orderColumn;
    private String infoType;

    public FlightData(String airline, Integer flightNumber, String startAirport, String destAirport, Date scheduledDepartureTime, Date scheduledArrivalTime, Date departureTime, Date arrivalTime, boolean diverted, boolean cancelled, Date cancellationTime, Date orderColumn, String infoType) {
        this.airline = airline;
        this.flightNumber = flightNumber;
        this.startAirport = startAirport;
        this.destAirport = destAirport;
        this.scheduledDepartureTime = scheduledDepartureTime;
        this.scheduledArrivalTime = scheduledArrivalTime;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.diverted = diverted;
        this.cancelled = cancelled;
        this.cancellationTime = cancellationTime;
        this.orderColumn = orderColumn;
        this.infoType = infoType;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public Integer getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(Integer flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getStartAirport() {
        return startAirport;
    }

    public void setStartAirport(String startAirport) {
        this.startAirport = startAirport;
    }

    public String getDestAirport() {
        return destAirport;
    }

    public void setDestAirport(String destAirport) {
        this.destAirport = destAirport;
    }

    public Date getScheduledDepartureTime() {
        return scheduledDepartureTime;
    }

    public void setScheduledDepartureTime(Date scheduledDepartureTime) {
        this.scheduledDepartureTime = scheduledDepartureTime;
    }

    public Date getScheduledArrivalTime() {
        return scheduledArrivalTime;
    }

    public void setScheduledArrivalTime(Date scheduledArrivalTime) {
        this.scheduledArrivalTime = scheduledArrivalTime;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public boolean isDiverted() {
        return diverted;
    }

    public void setDiverted(boolean diverted) {
        this.diverted = diverted;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Date getCancellationTime() {
        return cancellationTime;
    }

    public void setCancellationTime(Date cancellationTime) {
        this.cancellationTime = cancellationTime;
    }

    public Date getOrderColumn() {
        return orderColumn;
    }

    public void setOrderColumn(Date orderColumn) {
        this.orderColumn = orderColumn;
    }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }


    @Override
    public String toString(){
        return "FlightData{" +
                "airline='" + airline + '\'' +
                ", flightNumber=" + flightNumber +
                ", startAirport='" + startAirport + '\'' +
                ", destAirport='" + destAirport + '\'' +
                ", scheduledDepartureTime='" + scheduledDepartureTime + '\'' +
                ", scheduledArrivalTime='" + scheduledArrivalTime + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", diverted=" + diverted +
                ", cancelled=" + cancelled +
                ", cancellationTime='" + cancellationTime + '\'' +
                ", orderColumn='" + orderColumn + '\'' +
                ", infoType='" + infoType + '\'' +
                '}';
    }
}
