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
public class S3StorageInfoDTO {
    @JsonProperty("destination")
    public String Destination;

    @JsonProperty("region")
    public String Region;

    @JsonProperty("endpoint")
    public String Endpoint;

    @JsonProperty("enable_encryption")
    public Boolean EnableEncryption;

    @JsonProperty("encryption_type")
    public String EncryptionType;

    @JsonProperty("kms_key")
    public String KmsKey;

    @JsonProperty("canned_acl")
    public String CannedAcl;

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
