package com.myorg.dto;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Embeddable
public class SparkNodeDTO {
  @JsonProperty("private_ip")
  @Column(name="driver_private_ip")
  public String privateIp;

  @JsonProperty("public_dns")
  @Column(name="driver_public_dns")
  public String publicDns;

  @JsonProperty("node_id")
  @Column(name="driver_node_id")
  public String nodeId;

  @JsonProperty("instance_id")
  @Column(name="driver_instance_id")
  public String instanceId;

  @JsonProperty("start_timestamp")
  @Column(name="driver_start_timestamp")
  @Temporal(TemporalType.TIMESTAMP)
  public Date startTimestamp;

//  @Embedded
  @JsonProperty("node_aws_attributes")
  @Column(name="driver_node_aws_attributes")
  public SparkNodeAwsAttributesDTO nodeAwsAttributes;

  @JsonProperty("host_private_ip")
  @Column(name="driver_host_private_ip")
  public String hostPrivateIp;

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

