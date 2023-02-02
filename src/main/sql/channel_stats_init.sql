SET 'execution.runtime-mode' = 'streaming';
SET 'state.checkpoints.dir' = 'file:///tmp/flink-checkpoints/';
SET 'execution.checkpointing.mode' = 'EXACTLY_ONCE';
SET 'execution.time-characteristic'='event-time';
SET 'execution.checkpointing.interval' = '5min';
SET 'execution.checkpointing.min-pause' = '2min';
SET 'execution.checkpointing.max-concurrent-checkpoints' = '1';
SET 'execution.checkpointing.prefer-checkpoint-for-recovery' = 'true';
SET 'parallelism.default' = '-1';
--SET 'pipeline.max-parallelism' = '5';
SET 'pipeline.name' = 'Beacons';
SET 'jobmanager.scheduler' = 'adaptive';
SET 'cluster.declarative-resource-management.enabled' = 'true';
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
    WATERMARK for evntTm as evntTm - INTERVAL '2' SECOND
) with (
    'connector' = 'kafka',
    'topic' = 'quick-start-topic',
    'properties.bootstrap.servers' = 'localhost:29092',
    'scan.topic-partition-discovery.interval' = '60000',
    'properties.group.id' = 'grp5.8a1',
     'scan.startup.mode' = 'earliest-offset',
     'properties.auto.offset.reset' = 'earliest',
    'format' = 'json'
);

CREATE TABLE `channel_stats`(
    channel string,
    window_start_time timestamp,
    window_end_time timestamp,
    last_updated_time timestamp,
    user_count BIGINT,
    primary key (channel, window_start_time, window_end_time, last_updated_time) NOT ENFORCED
) with (
    'connector' = 'jdbc',
    'url' = 'jdbc:postgresql://127.0.0.1:5432/postgres',
    'table-name' = 'concurrent_users_by_channel',
    'username' = 'postgres',
    'password' = 'password'
);