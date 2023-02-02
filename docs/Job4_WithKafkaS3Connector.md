# Using Flink Kafka Connector and Kafka S3 connector

## Setup Kafka (Ignore if already done)
Follow the steps [here](Kafka_setup.md)

## Download Kafka Connector
```shell
confluent-hub install jcustenborder/kafka-connect-spooldir:2.0.64
```

## Prepare the connector config file
Example: kafka_s3_connector.properties
```shell
name = quick-start-s3-source
connector.class = io.confluent.connect.s3.source.S3SourceConnector
tasks.max = 1
value.converter = org.apache.kafka.connect.json.JsonConverter
mode = GENERIC
topics.dir = hnvcent/raw/beacon-logs/2022/09/01
topic.regex.list = quick-start-topic:.*
format.class = io.confluent.connect.s3.format.json.JsonFormat
s3.bucket.name = thunderstorm-firehose-dump
s3.region = us-east-1
s3.credentials.provider.class= com.amazonaws.auth.profile.ProfileCredentialsProvider
value.converter.schemas.enable = false
task.batch.size=2000
s3.poll.interval.ms=20000
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
The connector won’t reload data during the following scenarios:
> Renaming a file which the connector has already read. <br/>
> Uploading a newer version of a file with a new record.