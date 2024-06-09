package com.example.bigdata.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FlinkPropertiesUtil {

    public static String getDelay() {
        Properties properties = new Properties();
        try (InputStream input = FlinkPropertiesUtil.class.getClassLoader().getResourceAsStream("flink.properties")) {
            if (input != null) {
                properties.load(input);
                return properties.getProperty("delay");
            } else {
                System.out.println("Sorry, unable to find flink.properties");
                return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
