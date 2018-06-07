package com.myorg.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class S3StorageInfoDTO {
  @JsonProperty("destination")
  public String destination;

  @JsonProperty("region")
  public String region;

  @JsonProperty("endpoint")
  public String endpoint;

  @JsonProperty("enable_encryption")
  public Boolean enableEncryption;

  @JsonProperty("encryption_type")
  public String encryptionType;

  @JsonProperty("kms_key")
  public String kmsKey;

  @JsonProperty("canned_acl")
  public String cannedAcl;

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
