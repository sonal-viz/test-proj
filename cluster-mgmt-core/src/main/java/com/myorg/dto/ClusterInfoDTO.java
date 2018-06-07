package com.myorg.dto;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.myorg.util.ClusterPlatform;
import com.myorg.validation.CustomTagsValidator;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClusterInfoDTO {
  // These properties are only returned; they cannot be set
  @JsonProperty("cluster_id")
  public String clusterId;

  // implement custom validator for this
  // @NotBlank(message = "platform cannot be blank or null")
  @JsonProperty("platform")
  public ClusterPlatform platform;

  @JsonProperty("driver")
  public SparkNodeDTO driver;

  @JsonProperty("spark_context_id")
  public Long sparkContextId;

  @JsonProperty("executors")
  public SparkNodeDTO[] executors;

  @JsonProperty("jdbc_port")
  public Integer jdbcPort;

  @JsonProperty("state")
  public String state;

  @JsonProperty("state_message")
  public String stateMessage;

  @JsonProperty("cluster_created_by")
  public String clusterCreatedBy;

  @JsonProperty("cluster_source")
  public String clusterSource;

  @JsonProperty("start_time")
  public Date startTime;

  @JsonProperty("terminated_time")
  public Date terminatedTime;

  @JsonProperty("last_state_loss_time")
  public Date lastStateLossTime;

  @JsonProperty("last_activity_time")
  public Date lastActivityTime;

  @JsonProperty("cluster_memory_mb")
  public BigInteger clusterMemoryMb;

  @JsonProperty("cluster_cores")
  public BigInteger clusterCores;

  @JsonProperty("default_tags")
  public Map<String, String> defaultTags;

  @JsonProperty("cluster_log_status")
  public LogSyncStatusDTO clusterLogStatus;

  @JsonProperty("termination_reason")
  public TerminationReasonDTO terminationReason;

  // These properites can be set
  @JsonProperty("num_workers")
  public Integer numWorkers;

  @JsonProperty("autoscale")
  public AutoScaleDTO autoscale;

  @NotBlank(message = "cluster_name cannot be blank or null")
  @JsonProperty("cluster_name")
  public String clusterName;

  @JsonProperty("spark_version")
  public String sparkVersion;

  @JsonProperty("node_type_id")
  public String nodeTypeId;

  @JsonProperty("driver_node_type_id")
  public String driverNodeTypeId;

  @JsonProperty("aws_attributes")
  public AwsAttributesDTO awsAttributes;

  @JsonProperty("auto_termination_minutes")
  public Integer autoTerminationMinutes;

  @JsonProperty("enable_elastic_disk")
  public Boolean enableElasticDisk;

  @JsonProperty("spark_conf")
  public Map<String, String> sparkConf;

  @JsonProperty("ssh_public_keys")
  public String[] sshPublicKeys;

  @CustomTagsValidator
  @JsonProperty("custom_tags")
  public Map<String, String> customTags;

  @JsonProperty("cluster_log_conf")
  public ClusterLogConfDTO clusterLogConf;

  @JsonProperty("spark_env_vars")
  public Map<String, String> sparkEnvironmentVariables;

  @JsonProperty("creation_date")
  public Date creationDate;

  @JsonProperty("last_modification_date")
  public Date lastModificationDate;

  @JsonProperty("is_cached")
  public Boolean isCached;

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
