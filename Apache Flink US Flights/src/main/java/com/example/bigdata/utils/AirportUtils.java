package com.example.bigdata.utils;

import com.example.bigdata.model.AirportData;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class AirportUtils {
    public static Map<String, AirportData> getAirportsFromFile(String airportPath) {
        Map<String, AirportData> airports = new HashMap<>();
        try (Stream<Path> path = Files.walk(Paths.get(airportPath))) {
            path.filter(Files::isRegularFile)
                    .forEach(file -> {
                        try (Stream<String> lines = Files.lines(file)) {
                            lines.map(line -> line.split(","))
                                    .filter(array -> array.length == 14)
                                    .filter(array -> !array[0].startsWith("Airport ID"))
                                    .forEach(array -> {
                                        Integer airportId = Integer.parseInt(array[0]);
                                        String name = array[1];
                                        String city = array[2];
                                        String country = array[3];
                                        String iata = array[4];
                                        String icao = array[5];
                                        double latitude = Double.parseDouble(array[6]);
                                        double longitude = Double.parseDouble(array[7]);
                                        Integer altitude = Integer.parseInt(array[8]);
                                        Integer timezone = Integer.parseInt(array[9]);
                                        String dst = array[10];
                                        String timezoneName = array[11];
                                        String type = array[12];
                                        String state = array[13];
                                        AirportData airportData = new AirportData(airportId, name, city, country, iata, icao, latitude, longitude, altitude, timezone, dst, timezoneName, type, state);
                                        airports.put(airportData.getIata(), airportData);
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return airports;
    }
}
