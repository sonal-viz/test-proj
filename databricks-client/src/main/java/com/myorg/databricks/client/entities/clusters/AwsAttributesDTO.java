package com.myorg.databricks.client.entities.clusters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AwsAttributesDTO {
    @JsonProperty("first_on_demand")
    public Integer FirstOnDemand;

    @JsonProperty("availability")
    public String Availability;

    @JsonProperty("zone_id")
    public String ZoneId;

    @JsonProperty("instance_profile_arn")
    public String InstanceProfileARN;

    @JsonProperty("spot_bid_price_percent")
    public Integer SpotBidPricePercent;

    @JsonProperty("ebs_volume_type")
    public String EbsVolumeType;

    @JsonProperty("ebs_volume_count")
    public Integer EbsVolumeCount;

    @JsonProperty("ebs_volume_size")
    public Integer EbsVolumeSize;

    @Override
    public String toString() {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            return ow.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "Could Not Marshal Object to JSON";
        }
    }
}
