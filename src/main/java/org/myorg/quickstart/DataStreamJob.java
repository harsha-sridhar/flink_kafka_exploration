/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.myorg.quickstart;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.expressions.Expression;
import org.apache.flink.table.types.AbstractDataType;
import org.apache.flink.table.types.DataType;
import org.apache.flink.util.Collector;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.myorg.quickstart.model.TSV1HourlyBeacons;
import org.myorg.quickstart.utils.CustomDeserializationSchema;
import org.myorg.quickstart.utils.TSV1HourlyBeconsMapper;

import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.Properties;

/**
 * Skeleton for a Flink DataStream Job.
 *
 * <p>For a tutorial how to write a Flink application, check the
 * tutorials and examples on the <a href="https://flink.apache.org">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class DataStreamJob {

	public static void main(String[] args) throws Exception {
//		main1();
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
		DataStreamSource<String> input = env.readTextFile("/home/mh/Downloads/hnvcent-beacon-logs-1-2022-08-25-00-00-01-90cc6e86-3e86-479c-b7e1-d85d1433509c.json", "utf-8");

		input.flatMap(new TSV1HourlyBeconsMapper())
				.keyBy(value -> value.f0.getUa())
				.flatMap(new FlatMapFunction<Tuple1<TSV1HourlyBeacons>, Tuple2<String, Integer>>() {
					@Override
					public void flatMap(Tuple1<TSV1HourlyBeacons> tsv1HourlyBeaconsTuple1, Collector<Tuple2<String, Integer>> collector) throws Exception {
						collector.collect(new Tuple2<>(tsv1HourlyBeaconsTuple1.f0.getUa(), 1));
					}
				})
				.keyBy(key -> key.f0)
				.sum(1)
//				.map(value -> new String(value.f0+",")+value.f1)
				.writeAsCsv("output.csv", FileSystem.WriteMode.OVERWRITE);
//						.print();
//		env.setMaxParallelism(2);
		System.out.println(env.getExecutionPlan());
//		System.out.println(env.getStreamGraph().getJobGraph());

		env.execute("Flink Java API Skeleton");
//		Thread.currentThread().sleep(100000);

	}

	public static void main1()throws Exception{
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		KafkaSource<TSV1HourlyBeacons> kafkaSource =  KafkaSource.<TSV1HourlyBeacons>builder()
				.setBootstrapServers("localhost:9092")
				.setTopics("spooldir-json-topic")
				.setGroupId("group-2")
				.setStartingOffsets(OffsetsInitializer.earliest())
				.setValueOnlyDeserializer(new CustomDeserializationSchema())
				.build();

//		WatermarkStrategy<TSV1HourlyBeacons> customTime = WatermarkStrategy
//				.<TSV1HourlyBeacons>forBoundedOutOfOrderness(Duration.ofSeconds(20))
//				.withTimestampAssigner((event, timestamp) -> (long) Timestamp.valueOf(event.getEvntTm().toString()).getTime());
		DataStream<TSV1HourlyBeacons> sourceDatStream = env.fromSource(kafkaSource, WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(20)), "Kafka-source");
		StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(env);
		Table beaconTable =  tableEnvironment.fromDataStream(sourceDatStream,
				Schema.newBuilder()
						.column("assetID", DataTypes.STRING())
						.column("epgAssetID", DataTypes.STRING())
						.column("evntTm", DataTypes.TIMESTAMP(3))
						.column("segId", DataTypes.STRING())
						.column("time", DataTypes.TIMESTAMP(3))
						.column("uId", DataTypes.STRING())
						.column("ua", DataTypes.STRING())
						.column("ip", DataTypes.STRING())
				.build());
		beaconTable.printSchema();
		tableEnvironment.createTemporaryView("ts1_hourlybeacons", beaconTable);
//		Table result = tableEnvironment.sqlQuery("select COUNT(*) from ts1_hourlybeacons group by ip, ua");
		Table result = tableEnvironment.sqlQuery("select\n" +
				"    window_start, window_end,\n" +
				"    max(evntTm) as last_updated_time,\n" +
				"    count(distinct(ip||ua)) as user_cont\n" +
				"FROM TABLE(\n" +
				"    TUMBLE(TABLE ts1_hourlybeacons,\n" +
				"            DESCRIPTOR(evntTm), INTERVAL '1' MINUTES)\n" +
				")GROUP BY window_start, window_end");
//		tableEnvironment.executeSql("select\n" +
//				"    window_start, window_end,\n" +
//				"    max(evntTm) as last_updated_time,\n" +
//				"    count(distinct(ip||ua)) as user_cont\n" +
//				"FROM TABLE(\n" +
//				"    TUMBLE(TABLE ts1_hourlybeacons,\n" +
//				"            DESCRIPTOR(evntTm), INTERVAL '1' MINUTES)\n" +
//				")GROUP BY window_start, window_end");
		tableEnvironment.toChangelogStream(result).print();
//		System.out.println(env.getExecutionPlan());
		System.out.println(env.getStreamGraph().getJobGraph());
//		tableEnvironment.toChangelogStream(result).print();

//				.print();
		env.execute("myJob");
	}

	public static void main2(){
		StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
		StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(executionEnvironment);
		tableEnvironment.executeSql("CREATE TABLE source_table(" +
				" `evntTm` timestamp(3), ip varchar, ua varchar, " +
				" WATERMARK for evntTm as evntTm - INTERVAL '10' SECOND)");
	}
}
