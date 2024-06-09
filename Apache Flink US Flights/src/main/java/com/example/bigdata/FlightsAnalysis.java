package com.example.bigdata;

import com.example.bigdata.model.AirportData;
import com.example.bigdata.model.FlightAnomalyData;
import com.example.bigdata.model.FlightData;
import com.example.bigdata.model.FlightDataAgg;
import com.example.bigdata.utils.*;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import java.text.SimpleDateFormat;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;

import static com.example.bigdata.connectors.Connectors.getMySQLSink;

public class FlightsAnalysis {
    public static void main(String[] args) throws Exception {

        ParameterTool propertiesFromFile = ParameterTool.fromPropertiesFile("src/main/resources/flink.properties");
        ParameterTool propertiesFromArgs = ParameterTool.fromArgs(args);
        ParameterTool properties = propertiesFromFile.mergeWith(propertiesFromArgs);
        int D = properties.getInt("D", 60);
        int N = properties.getInt("N", 30);

        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
                "flights-in-us",
                new SimpleStringSchema(),
                properties.getProperties()
        );

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String airportPath = properties.get("airports.uri");
        String delay = properties.get("delay", "A");
        Map<String, AirportData> airports = AirportUtils.getAirportsFromFile(airportPath);
        DataStream<String> inputStream = env.addSource(consumer);
        DataStream<FlightData> flightDataDS = inputStream
                .map((MapFunction<String, String[]>) txt -> txt.split(","))
                .filter(array -> array.length == 25)
                .filter(array -> !array[0].startsWith("airline"))
                .filter(array -> DateUtils.parseDateTime(array[23], new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), array[24], "orderColumn") != null)
                .map(array -> {
                    String airline = array[0];
                    Integer flightNumber = array[1].isEmpty() ? 0 : Integer.parseInt(array[1]);
                    String startAirport = array[3];
                    String destAirport = array[4];
                    SimpleDateFormat isoFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                    Date scheduledDepartureTime = isoFormatter.parse(array[5]);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strScheduledArrivalTime = array[8];
                    String strDepartureTime = array[9];
                    String strArrivalTime = array[13];
                    Boolean diverted = Boolean.parseBoolean(array[14]);
                    Boolean cancelled = Boolean.parseBoolean(array[15]);
                    String strCancellationTime = array[22];
                    String strOrderColumn = array[23];
                    String infoType = array[24];
                    Date scheduledArrivalTime = DateUtils.parseDateTime(strScheduledArrivalTime, formatter, infoType, "scheduledArrivalTime");
                    Date departureTime = DateUtils.parseDateTime(strDepartureTime, formatter, infoType, "departureTime");
                    Date arrivalTime = DateUtils.parseDateTime(strArrivalTime, formatter, infoType, "arrivalTime");
                    Date cancellationTime = DateUtils.parseDateTime(strCancellationTime, formatter, infoType, "cancellationTime");
                    Date orderColumn = DateUtils.parseDateTime(strOrderColumn, formatter, infoType, "orderColumn");
//                    orderColumn = orderColumn == null ? scheduledDepartureTime : orderColumn;
                    // print FlightData
//                    System.out.println("FlightData: " + airline + " " + flightNumber + " " + startAirport + " " + destAirport + " " + scheduledDepartureTime + " " + scheduledArrivalTime + " " + departureTime + " " + arrivalTime + " " + diverted + " " + cancelled + " " + cancellationTime + " " + orderColumn + " " + infoType);

                return new FlightData(airline, flightNumber, startAirport, destAirport, scheduledDepartureTime, scheduledArrivalTime, departureTime, arrivalTime, diverted, cancelled, cancellationTime, orderColumn, infoType);
                }).assignTimestampsAndWatermarks(WatermarkStrategy
                        .<FlightData>forBoundedOutOfOrderness(Duration.ofMinutes(5))
                        .withTimestampAssigner((event, timestamp) -> event.getOrderColumn().getTime()))
                        .process(new ProcessFunction<FlightData, FlightData>() {
            @Override
            public void processElement(FlightData value, Context ctx, Collector<FlightData> out) throws Exception {
                long watermark = ctx.timerService().currentWatermark();
//                System.out.println("Current watermark: " + watermark);
                out.collect(value);
                }
        });



        DataStream<FlightDataAgg> flightDataAggDS = flightDataDS
                .map(fd -> {
                    String state = airports.get(fd.getStartAirport()).getState();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String day = dateFormat.format(fd.getOrderColumn());
                    FlightDataAgg agg = new FlightDataAgg(state, day,0L, 0L, 0L, 0L);

                    agg.addDeparture(FlightUtils.getDelay(fd.getDepartureTime(), fd.getScheduledDepartureTime()));
                    agg.addArrival(FlightUtils.getDelay(fd.getArrivalTime(), fd.getScheduledArrivalTime()));

                    return agg;
                })
                .keyBy((KeySelector<FlightDataAgg, String>) FlightDataAgg::getState)
                .window(new DayWindowAssigner(delay))
                .apply(new FinalWindowResult());

        DataStream<FlightAnomalyData> anomalyDataStream = flightDataDS
                .keyBy(FlightData::getDestAirport)
                .window(SlidingEventTimeWindows.of(Time.minutes(D), Time.minutes(10)))
                .process(new AnomalyDetectionProcessFunction(airports, N));

//        flightDataAggDS.print();
        anomalyDataStream.map((MapFunction<FlightAnomalyData, String>) Object::toString).sinkTo(KafkaSink.<String>builder()
                .setBootstrapServers(properties.get("bootstrap.servers"))
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("flight-anomalies")
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()
                ).setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build());
        flightDataAggDS.addSink(getMySQLSink(properties));

        env.execute("FlightsAnalysis");
    }
    }

