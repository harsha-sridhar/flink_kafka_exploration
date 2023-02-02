package org.myorg.quickstart.exploration;


import org.apache.flink.configuration.Configuration;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


public class BatchRun {

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        JsonFileConsumerConfig consumerConfig = new JsonFileConsumerConfig(TSV1HourlyBeacons.class);
//        consumerConfig.setPath("/home/mh/Documents/Amagi/Flink/data/2022/");
//        DataStream<TSV1HourlyBeacons> dataStream = new JsonFileConsumer(consumerConfig).getJsonFileStream(env);
//        dataStream.print();

//        TableEnvironment
        //        dataStream.flatMap(value -> new Tuple2<String, Integer>(value.getUa(), 1))
//                .keyBy(value -> value[0])
//                .sum(1)
//                .print();
    }
}
