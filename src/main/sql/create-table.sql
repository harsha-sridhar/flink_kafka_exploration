SET sql-client.execution.result-mode=TABLE;
SET 'parallelism.default' = '5';
CREATE TABLE `default_database`.`tsv1_hourly_beacons`(
    assetID varchar,
    epgAssetID varchar,
    evntTm timestamp(3),
    ip varchar,
    segId varchar,
    `time` string,
    uId varchar,
    ua varchar,

    WATERMARK for evntTm as evntTm
)
 with (
    'connector' = 'filesystem',
    'path' = '/tmp/data/',
    'format' = 'json'
);
