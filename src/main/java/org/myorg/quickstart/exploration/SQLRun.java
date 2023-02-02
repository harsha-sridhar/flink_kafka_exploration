package org.myorg.quickstart.exploration;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironmentFactory;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.myorg.quickstart.model.TSV1HourlyBeacons;
import org.myorg.quickstart.utils.CustomDeserializationSchema;

import java.time.Duration;

public class SQLRun {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        KafkaSource<TSV1HourlyBeacons> kafkaSource =  KafkaSource.<TSV1HourlyBeacons>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("quick-start-topic")
                .setGroupId("group-2")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new CustomDeserializationSchema())
                .build();
        DataStream<TSV1HourlyBeacons> hourlyBeaconsDataStream = env.fromSource(kafkaSource, WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(20)),"kafka-source");
        StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(env);
        Table beaconTable =  tableEnvironment.fromDataStream(hourlyBeaconsDataStream);
        tableEnvironment.createTemporaryView("ts1_hourlybeacons", beaconTable);
//        Table result = tableEnvironment.sqlQuery("select\n" +
//                "    window_start, window_end,\n" +
//                "    max(evntTm) as last_updated_time,\n" +
//                "    count(distinct(ip||ua)) as user_cont\n" +
//                "FROM TABLE(\n" +
//                "    TUMBLE(TABLE ts1_hourlybeacons,\n" +
//                "            DESCRIPTOR(evntTm), INTERVAL '1' MINUTES)\n" +
//                ")GROUP BY window_start, window_end");
        tableEnvironment.executeSql("select\n" +
                "    window_start, window_end,\n" +
                "    max(evntTm) as last_updated_time,\n" +
                "    count(distinct(ip||ua)) as user_cont\n" +
                "FROM TABLE(\n" +
                "    TUMBLE(TABLE ts1_hourlybeacons,\n" +
                "            DESCRIPTOR(evntTm), INTERVAL '1' MINUTES)\n" +
                ")GROUP BY window_start, window_end").print();
        System.out.println(env.getExecutionPlan());
    }
}
