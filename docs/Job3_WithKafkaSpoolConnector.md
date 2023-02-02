# Using Flink Kafka Connector and Kafka JSON Schemaless connector

## Setup Kafka (Ignore if already done)
Follow the steps [here](Kafka_setup.md)

## Download Kafka Connector
```shell
confluent-hub install jcustenborder/kafka-connect-spooldir:2.0.64
```

## Prepare the connector config file
Example: kafka_json_source_connector.properties<br/>
> <b>WARNING:</b> Please be cautious with the option `cleanup.policy=NONE|MOVE|DELETE|MOVEBYDATE`<br>
If this option is not specified, then it defaults to DELETE, which would delete all 
the files in the provided path<br/>
Value `NONE` seems to cause a bug due to which the files are processed again and again<br>

**Note**: the directory path specified by `error.path` needs to exist before starting the connector,
and in case the `cleanup.policy=MOVE|MOVEBYDATE` then the directory specified by `finished.path` needs to exist before starting the connector
```shell
name=JsonSpoolDir2
tasks.max=1
connector.class=com.github.jcustenborder.kafka.connect.spooldir.SpoolDirSchemaLessJsonSourceConnector
input.path=/home/mh/Documents/Amagi/Flink/s3_data/
input.path.walk.recursively=true
#input.file.pattern=^.*.gz$
error.path=/tmp/beacon_error/
finished.path=/home/mh/Documents/Amagi/Flink/s3_data_finished/
halt.on.error=false
topic=spooldir-json-topic2
cleanup.policy=MOVE
value.converter=org.apache.kafka.connect.storage.StringConverter
json.file_reader.json.compression.type=gzip
```

## Start confluent kafka and related components (If not already running)
```shell
confluent local services start
```

## Upload the connector config file
via the web-ui hosted at localhost:9021
Connector status should show as `Running` and the list of topics should show the
created topic with data

## Issues
The option `cleanup.policy=NONE` causes the topic to be flooded with duplicate messages due to reprocessing of the files<br/>
In most cases, moving the file is not appropriate 