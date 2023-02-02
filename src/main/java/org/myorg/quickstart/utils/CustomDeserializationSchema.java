package org.myorg.quickstart.utils;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.myorg.quickstart.model.TSV1HourlyBeacons;

import java.io.IOException;

public class CustomDeserializationSchema implements DeserializationSchema<TSV1HourlyBeacons> {

    static final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public TSV1HourlyBeacons deserialize(byte[] bytes) throws IOException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(bytes, TSV1HourlyBeacons.class);
    }

    @Override
    public boolean isEndOfStream(TSV1HourlyBeacons tsv1HourlyBeacons) {
        return false;
    }

    @Override
    public TypeInformation<TSV1HourlyBeacons> getProducedType() {
        return TypeInformation.of(TSV1HourlyBeacons.class);
    }
}
