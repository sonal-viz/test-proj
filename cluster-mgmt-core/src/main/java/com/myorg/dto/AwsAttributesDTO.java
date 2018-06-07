package com.myorg.dto;

import java.util.ArrayList;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Embeddable
public class AwsAttributesDTO {
  @JsonProperty("first_on_demand")
  @Column(name="aws_attributes_first_on_demand")
  public Integer firstOnDemand;

  @JsonProperty("availability")
  @Column(name="aws_attributes_availability")
  public String availability;

  @JsonProperty("zone_id")
  @Column(name="aws_attributes_zone_id")
  public String zoneId;

  @JsonProperty("instance_profile_arn")
  @Column(name="aws_attributes_instance_profile_arn")
  public String instanceProfileArn;

  @JsonProperty("spot_bid_price_percent")
  @Column(name="aws_attributes_spot_bid_price_percent")
  public Integer spotBidPricePercent;
  
  @JsonProperty("access_key")
  @Column(name="aws_attributes_access_key")
  public String accessKey;
  
  @JsonProperty("secret")
  @Column(name="aws_attributes_secret")
  public String secret;
  
  @JsonProperty("private_key_name")
  @Column(name="aws_attributes_private_key_name")
  public String privateKeyName;
  
  @JsonProperty("emr_role")
  @Column(name="aws_attributes_emr_role")
  public String emrRole;
  
  @JsonProperty("emr_resize_role")
  @Column(name="aws_attributes_emr_resize_role")
  public String emrResizeRole;
  
  @JsonProperty("master_bid_price")
  @Column(name="aws_attributes_master_bid_price")
  public Double masterBidPrice;
  
  @JsonProperty("slave_bid_price")
  @Column(name="aws_attributes_slave_bid_price")
  public Double slaveBidPrice;

  @JsonProperty("ebs_volume_type")
  @Column(name="aws_attributes_ebs_volume_type")
  public String ebsVolumeType;

  @JsonProperty("ebs_volume_count")
  @Column(name="aws_attributes_ebs_volume_count")
  public Integer ebsVolumeCount;

  @JsonProperty("ebs_volume_size")
  @Column(name="aws_attributes_ebs_volume_size")
  public Integer ebsVolumeSize;
  
  @Lob
  @JsonProperty("emr_steps")
  @Column(name="aws_attributes_emr_steps")
  public ArrayList<EMRStep> emrSteps;
  
  @JsonProperty("region")
  @Column(name="aws_attributes_region")
  public String region;
  
  @JsonProperty("emr_master_security_group")
  @Column(name="aws_attributes_emr_master_security_group")
  public String emrMasterSecurityGroup;
  
  @JsonProperty("emr_slave_security_group")
  @Column(name="aws_attributes_emr_slave_security_group")
  public String emrSlaveSecurityGroup;

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
