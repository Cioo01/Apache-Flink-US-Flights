package com.example.bigdata.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static Date parseDateTime(String dateTimeStr, SimpleDateFormat formatter, String infoType, String fieldType) {
        if (dateTimeStr == null || dateTimeStr.isEmpty() || "N/A".equals(dateTimeStr)) {
            return null;
        }
        try {
            switch (infoType) {
                case "D":
                    if ("arrivalTime".equals(fieldType) || "cancellationTime".equals(fieldType)) {
                        return null;
                    }
                    break;
                case "A":
                    if ("cancellationTime".equals(fieldType)) {
                        return null;
                    }
                    break;
                case "C":
                    if (!"cancellationTime".equals(fieldType)) {
                        return null;
                    }
                    break;
            }
            return formatter.parse(dateTimeStr);
        } catch (ParseException e) {
            return null;
        }
    }
}
