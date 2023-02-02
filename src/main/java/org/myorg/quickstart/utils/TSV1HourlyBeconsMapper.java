package org.myorg.quickstart.utils;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.util.Collector;
import org.myorg.quickstart.model.TSV1HourlyBeacons;

public class TSV1HourlyBeconsMapper implements FlatMapFunction<String, Tuple1<TSV1HourlyBeacons>> {
    @Override
    public void flatMap(String s, Collector<Tuple1<TSV1HourlyBeacons>> collector) throws Exception {
        collector.collect(new Tuple1<TSV1HourlyBeacons>(Mapper.getObject(s, TSV1HourlyBeacons.class)));
    }
}
