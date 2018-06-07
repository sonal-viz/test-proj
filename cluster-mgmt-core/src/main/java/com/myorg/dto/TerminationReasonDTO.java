package com.myorg.dto;

import java.util.Map;
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
public class TerminationReasonDTO {
  @JsonProperty("code")
  @Column(name="termination_reason_code")
  public String terminationCode;

  @JsonProperty("parameters")
  @Column(name="termination_reason_parameters") @Lob
  public Map<String, String> parameters;

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
