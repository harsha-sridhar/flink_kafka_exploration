package org.myorg.quickstart.exploration;

public class TestRun {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStream<Tuple2<String, Long>> sourceStream = env.fromElements(
                new Tuple2<>("a",1L),
                new Tuple2<>("b",2L),
                new Tuple2<>("c",3L),
                new Tuple2<>("e",5L),
                new Tuple2<>("d",4L),
                new Tuple2<>("f",6L),
                new Tuple2<>("g",7L),
                new Tuple2<>("h",8L),
                new Tuple2<>("i",9L),
                new Tuple2<>("j",10L),
                new Tuple2<>("k",12L)
        );

        sourceStream.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Tuple2<String, Long>>() {
                    @Override
                    public long extractAscendingTimestamp(Tuple2<String, Long> stringLongTuple2) {
                        return stringLongTuple2.f1;
                    }
                }).windowAll(TumblingEventTimeWindows.of(Time.seconds(5))).allowedLateness(Time.seconds(1))
                .process(new ProcessAllWindowFunction<Tuple2<String, Long>, String, TimeWindow>() {
                    @Override
                    public void process(ProcessAllWindowFunction<Tuple2<String, Long>, String, TimeWindow>.Context context, Iterable<Tuple2<String, Long>> iterable, Collector<String> collector) throws Exception {
                        String elements = "";
                        for(Tuple2<String, Long> element: iterable){
                            elements += element.f0 +",";
                        }
                        collector.collect(elements);
                    }
                }).print();
        env.execute("testjob");
    }
}
