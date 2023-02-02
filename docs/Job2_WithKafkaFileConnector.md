# Using Flink Kafka Connector and Kafka File Source Connector

## Setup Kafka (Ignore if already done)
Follow the steps [here](Kafka_setup.md)
## Download kafka file source connector
```shell
confluent-hub install mmolimar/kafka-connect-fs:1.3.0
```

## Prepare the connector config file
Example: fs_source_kafka_connector.properties
```shell
name = FsSourceConnector
connector.class = com.github.mmolimar.kafka.connect.fs.FsSourceConnector
tasks.max = 5
key.converter = org.apache.kafka.connect.storage.StringConverter
value.converter = org.apache.kafka.connect.storage.StringConverter
fs.uris = /home/mh/Documents/Amagi/Flink/data1/
topic = my-topic-v2
policy.regexp = .*
policy.class = com.github.mmolimar.kafka.connect.fs.policy.SimplePolicy
file_reader.batch_size = 0
file_reader.class = com.github.mmolimar.kafka.connect.fs.file.reader.TextFileReader
policy.recursive = true
policy.cleanup = none
policy.batch_size = 0
```

## Start confluent kafka and related components
```shell
confluent local services start
```

## Upload the connector config file 
via the web-ui hosted at localhost:9021
Connector status should show as `Running` and the list of topics should show the
created topic with data

## Issue with this connector
Converts all the key value pairs to `org.apache.kafka.connect.data.Struct`
type even though the value converter is specified as StringConverter . <br/>
This would mean this data structure needs to be deserialized to json by the consumer.
