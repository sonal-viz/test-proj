package com.myorg.dto;

import java.io.Serializable;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EMRStep implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = 8213405254508450412L;
  
  @JsonProperty("name")
  public String name;
  
  @JsonProperty("type")
  public String type;

  @JsonProperty("location")
  public String location;
  
  @JsonProperty("action_on_failure")
  public String actionOnFailure;

  @JsonProperty("arguments")
  public ArrayList<String> arguments;
}
