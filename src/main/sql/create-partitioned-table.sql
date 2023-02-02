--SET sql-client.execution.result-mode=TABLE;
--SET 'parallelism.default' = '-1';
--SET 'scheduler-mode'='adaptive';
--ADD JAR '/home/mh/Downloads/parallelgzip-1.0.5.jar';
--SET 'execution.savepoint.path' = '/tmp/flink-savepoints/savepoint-cca7bc-bb1e257f0dab';
CREATE TABLE `default_database`.`tsv1_hourly_beacons`(
    assetID varchar,
    epgAssetID varchar,
    evntTm timestamp(3),
    ip varchar,
    segId varchar,
    `time` string,
    chn varchar,
    uId varchar,
    ua varchar,
    user_id as ip||ua,
    `year` varchar,
    `month` varchar,
    `day` varchar,
    `hour` varchar,
    WATERMARK for evntTm as evntTm - INTERVAL '5' SECOND
)
PARTITIONED BY (`year`,`month`,`day`,`hour`)
 with (
    'connector' = 'filesystem',
    'path' = '/home/mh/Documents/Amagi/Flink/s3_data_finished',
    'format' = 'json',
    'source.monitor-interval' = '1'
);

CREATE TABLE `concurrent_users`(
    window_start_time timestamp,
    window_end_time timestamp,
    last_updated_time timestamp ,
    user_count BIGINT,
    primary key (window_start_time, window_end_time, last_updated_time) NOT ENFORCED
) with (
    'connector' = 'jdbc',
    'url' = 'jdbc:postgresql://127.0.0.1:5432/postgres',
    'table-name' = 'concurrent_users_fs',
    'username' = 'postgres',
    'password' = 'password'
);
