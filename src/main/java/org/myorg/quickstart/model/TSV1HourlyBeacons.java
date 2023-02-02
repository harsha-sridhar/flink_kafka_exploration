package org.myorg.quickstart.model;

import lombok.*;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.sql.Timestamp;


@JsonSerialize
@JsonIgnoreProperties
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class TSV1HourlyBeacons {

    @JsonProperty
    private String assetID;

    @JsonProperty
    private String epgAssetID;

    @JsonProperty
    private String evntTm;

    @JsonProperty
    private String ip;

    @JsonProperty
    private String segId;

    @JsonProperty
    private String time;

    @JsonProperty
    private String uId;

    @JsonProperty
    private String ua;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
