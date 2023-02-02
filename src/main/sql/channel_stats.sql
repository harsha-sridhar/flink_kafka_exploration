insert into channel_stats
select
    chn, window_start,window_end,
    max(evntTm) as last_updated_time,
    count(distinct(user_id)) as user_count
FROM TABLE(
         TUMBLE(TABLE default_catalog.default_database.tsv1_hourly_beacons,
                 DESCRIPTOR(evntTm), INTERVAL '1' MINUTES)
     )GROUP BY chn, window_start, window_end