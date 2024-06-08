package com.example.bigdata.utils;

import java.util.Date;

public class FlightUtils {
    public static Long getDelay(Date actualTime, Date scheduledTime) {
        if (actualTime != null && scheduledTime != null) {
            long delay = actualTime.getTime() - scheduledTime.getTime();
            return (Long) (delay / (1000 * 60));
        }
        return 0L;
    }
}
