# File System Source Connector
### Prepare the input/ output table(s) initialization script
Example: init.sql
```mysql
CREATE TABLE sample_table(
    column1 varchar,
    column2 varchar,
    eventTime timestamp(3),
    WATERMARK for eventTime as eventTime - INTERVAL '5' SECOND
) 
[ PARTITIONED BY (parition_col1, partition_col2, ...)]
with (
    'connector' = 'filesystem',
    'path' = '<path_to_your_files>',
    'format' = 'json',
    'source.monitor-interval' = '1' #Exclude this config key for bounded file stream
);

CREATE TABLE sink_table(
    col1 varchar,
    col2 bigint
)
with (
    'connector'='jdbc',
    'url' = 'url_to_database',
    'table_name' = 'name_of_table',
    'username' = 'username',
    'password' = '*******'
);
```

### Prepare the sql script to execute
Example: query.sql
```mysql
INSERT INTO sink_table
SELECT 
    window_end, count(*) 
FROM TABLE(
    TUMBLE(TABLE sample_table, DESCRIPTOR(eventTime), INTERVAL '1' MINUTES)
) GROUP BY window_start, window_end 
```

### Submit the job to flink cluster using sql-client
Start the Flink cluster if not yet started
```shell
cd $FLINK_HOME/bin 
./start-cluster.sh
```
Submit the job to cluster
```shell
./sql-client -i init.sql -f query.sql
```
Upon successful job submission, it returns a job id which can be used to monitor the status of the job
