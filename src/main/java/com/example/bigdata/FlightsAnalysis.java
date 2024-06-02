package com.example.bigdata;

import org.apache.flink.api.java.utils.ParameterTool;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class FlightsAnalysis {
    public static void main(String[] args) throws Exception {

        ParameterTool propertiesFromFile = ParameterTool.fromPropertiesFile("src/main/resources/flink.properties");
        ParameterTool propertiesFromArgs = ParameterTool.fromArgs(args);
        ParameterTool properties = propertiesFromFile.mergeWith(propertiesFromArgs);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();


//        DataStream<SensorData> sensorDataDS = inputStream.map((MapFunction<String, String[]>) txt -> txt.split(",") )
//                .filter(array -> array.length == 3)
//                .filter(array -> array[0].matches("\\d+") && array[2].matches("\\d+"))
//                .map(array -> new SensorData(Integer.parseInt(array[0]), array[1], Long.parseLong(array[2])));

//        sensorDataDS.print();

        env.execute("FlightsAnalysis");
    }
}
