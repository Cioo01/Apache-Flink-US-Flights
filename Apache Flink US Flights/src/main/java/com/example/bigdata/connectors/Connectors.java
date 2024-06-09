package com.example.bigdata.connectors;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import com.example.bigdata.model.FlightDataAgg;
import java.sql.PreparedStatement;
import java.sql.SQLException;


import java.time.Duration;
public class Connectors {
    public static SinkFunction<FlightDataAgg> getMySQLSink(ParameterTool properties) {
        JdbcStatementBuilder<FlightDataAgg> statementBuilder =
                new JdbcStatementBuilder<FlightDataAgg>() {
                    @Override
                    public void accept(PreparedStatement ps, FlightDataAgg data) throws SQLException {
                        ps.setString(1, data.getState());
                        ps.setString(2, data.getDay());
                        ps.setLong(3, data.getTotalDepartures());
                        ps.setLong(4, data.getTotalDeparturesDelay());
                        ps.setLong(5, data.getTotalArrivals());
                        ps.setLong(6, data.getTotalArrivalsDelay());
                    }
                };
        JdbcConnectionOptions connectionOptions = new
                JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                .withUrl(properties.getRequired("mysql.url"))
                .withDriverName("com.mysql.jdbc.Driver")
                .withUsername(properties.getRequired("mysql.username"))
                .withPassword(properties.getRequired("mysql.password"))
                .build();
        JdbcExecutionOptions executionOptions = JdbcExecutionOptions.builder()
                .withBatchSize(100)
                .withBatchIntervalMs(200)
                .withMaxRetries(5)
                .build();
        SinkFunction<FlightDataAgg> jdbcSink =
                JdbcSink.sink("insert into us_flights_sink" +
                                "(us_state, day, total_departures, " +
                                "total_departures_delay, total_arrivals, total_arrivals_delay) \n" +
                                "values (?, ?, ?, ?, ?, ?)",
                        statementBuilder,
                        executionOptions,
                        connectionOptions);
        return jdbcSink;
    }

}
