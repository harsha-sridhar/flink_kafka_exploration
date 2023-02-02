package org.myorg.quickstart.exploration;



import org.apache.flink.api.common.serialization.SimpleStringSchema;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.api.common.serialization.DeserializationSchema;

import java.util.Properties;

public class TblExploration {

    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers","localhost:9092");
//        FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<String>("my-topic-v1",new SimpleStringSchema(), properties);
//        env.addSource(kafkaConsumer).print();

    }
}
