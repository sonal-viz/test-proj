package com.myorg.entity;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.myorg.dto.AutoScaleDTO;
import com.myorg.dto.AwsAttributesDTO;
import com.myorg.dto.LogSyncStatusDTO;
import com.myorg.dto.SparkNodeDTO;
import com.myorg.dto.TerminationReasonDTO;
import com.myorg.util.ClusterPlatform;

@Entity
public class ClusterInfo {
  @Id
  private String clusterId;

  @Enumerated(EnumType.STRING)
  private ClusterPlatform platform;

  @Embedded
  private SparkNodeDTO driver;

  private Long sparkContextId;
  //
  // @Lob
  // @AttributeOverride(name = "executors", column = @Column(name = "executors"))
  // private SparkNodeDTO[] executors;

  private Integer jdbcPort;

  private String state;

  private String stateMessage;

  private String clusterCreatedBy;

  private String clusterSource;

  @Temporal(TemporalType.TIMESTAMP)
  private Date startTime;

  @Temporal(TemporalType.TIMESTAMP)
  private Date terminatedTime;

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastStateLossTime;

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastActivityTime;

  private BigInteger clusterMemoryMb;

  private BigInteger clusterCores;

  @Lob
  private HashMap<String, String> defaultTags;

  @Embedded
  private LogSyncStatusDTO clusterLogStatus;

  @Embedded
  private TerminationReasonDTO terminationReason;

  // These properites can be set
  private Integer numWorkers;

  @Embedded
  private AutoScaleDTO autoScale;

  private String clusterName;

  private String sparkVersionKey;

  private String nodeTypeId;

  private String driverNodeTypeId;

  @Embedded
  private AwsAttributesDTO awsAttributes;

  private Integer autoTerminationMinutes;

  private Boolean enableElasticDisk;

  @Lob
  private HashMap<String, String> sparkConf;

  private String[] sshPublicKeys;

  @Lob
  private HashMap<String, String> customTags;

  private String clusterLogConf;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "creation_date")
  private Date creationDate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_modification_date")
  private Date lastModificationDate;

  private Boolean isCached;

  @Lob
  private HashMap<String, String> sparkEnvironmentVariables;

  public String getClusterId() {
    return clusterId;
  }

  public void setClusterId(String clusterId) {
    this.clusterId = clusterId;
  }

  public ClusterPlatform getPlatform() {
    return platform;
  }

  public void setPlatform(ClusterPlatform platform) {
    this.platform = platform;
  }

  public SparkNodeDTO getDriver() {
    return driver;
  }

  public void setDriver(SparkNodeDTO driver) {
    this.driver = driver;
  }

  public Long getSparkContextId() {
    return sparkContextId;
  }

  public void setSparkContextId(Long sparkContextId) {
    this.sparkContextId = sparkContextId;
  }

  // public SparkNodeDTO[] getExecutors() {
  // return executors;
  // }
  //
  // public void setExecutors(SparkNodeDTO[] executors) {
  // this.executors = executors;
  // }

  public Integer getJdbcPort() {
    return jdbcPort;
  }

  public void setJdbcPort(Integer jdbcPort) {
    this.jdbcPort = jdbcPort;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getStateMessage() {
    return stateMessage;
  }

  public void setStateMessage(String stateMessage) {
    this.stateMessage = stateMessage;
  }

  public String getClusterCreatedBy() {
    return clusterCreatedBy;
  }

  public void setClusterCreatedBy(String clusterCreatedBy) {
    this.clusterCreatedBy = clusterCreatedBy;
  }

  public String getClusterSource() {
    return clusterSource;
  }

  public void setClusterSource(String clusterSource) {
    this.clusterSource = clusterSource;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getTerminatedTime() {
    return terminatedTime;
  }

  public void setTerminatedTime(Date terminatedTime) {
    this.terminatedTime = terminatedTime;
  }

  public Date getLastStateLossTime() {
    return lastStateLossTime;
  }

  public void setLastStateLossTime(Date lastStateLossTime) {
    this.lastStateLossTime = lastStateLossTime;
  }

  public Date getLastActivityTime() {
    return lastActivityTime;
  }

  public void setLastActivityTime(Date lastActivityTime) {
    this.lastActivityTime = lastActivityTime;
  }

  public BigInteger getClusterMemoryMb() {
    return clusterMemoryMb;
  }

  public void setClusterMemoryMb(BigInteger clusterMemoryMb) {
    this.clusterMemoryMb = clusterMemoryMb;
  }

  public BigInteger getClusterCores() {
    return clusterCores;
  }

  public void setClusterCores(BigInteger clusterCores) {
    this.clusterCores = clusterCores;
  }

  public HashMap<String, String> getDefaultTags() {
    return defaultTags;
  }

  public void setDefaultTags(HashMap<String, String> defaultTags) {
    this.defaultTags = defaultTags;
  }

  public LogSyncStatusDTO getClusterLogStatus() {
    return clusterLogStatus;
  }

  public void setClusterLogStatus(LogSyncStatusDTO clusterLogStatus) {
    this.clusterLogStatus = clusterLogStatus;
  }

  public TerminationReasonDTO getTerminationReason() {
    return terminationReason;
  }

  public void setTerminationReason(TerminationReasonDTO terminationReason) {
    this.terminationReason = terminationReason;
  }

  public Integer getNumWorkers() {
    return numWorkers;
  }

  public void setNumWorkers(Integer numWorkers) {
    this.numWorkers = numWorkers;
  }

  public AutoScaleDTO getAutoScale() {
    return autoScale;
  }

  public void setAutoScale(AutoScaleDTO autoScale) {
    this.autoScale = autoScale;
  }

  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public String getSparkVersionKey() {
    return sparkVersionKey;
  }

  public void setSparkVersionKey(String sparkVersionKey) {
    this.sparkVersionKey = sparkVersionKey;
  }

  public String getNodeTypeId() {
    return nodeTypeId;
  }

  public void setNodeTypeId(String nodeTypeId) {
    this.nodeTypeId = nodeTypeId;
  }

  public String getDriverNodeTypeId() {
    return driverNodeTypeId;
  }

  public void setDriverNodeTypeId(String driverNodeTypeId) {
    this.driverNodeTypeId = driverNodeTypeId;
  }

  public AwsAttributesDTO getAwsAttributes() {
    return awsAttributes;
  }

  public void setAwsAttributes(AwsAttributesDTO awsAttributes) {
    this.awsAttributes = awsAttributes;
  }

  public Integer getAutoTerminationMinutes() {
    return autoTerminationMinutes;
  }

  public void setAutoTerminationMinutes(Integer autoTerminationMinutes) {
    this.autoTerminationMinutes = autoTerminationMinutes;
  }

  public Boolean getEnableElasticDisk() {
    return enableElasticDisk;
  }

  public void setEnableElasticDisk(Boolean enableElasticDisk) {
    this.enableElasticDisk = enableElasticDisk;
  }

  public HashMap<String, String> getSparkConf() {
    return sparkConf;
  }

  public void setSparkConf(HashMap<String, String> sparkConf) {
    this.sparkConf = sparkConf;
  }

  public String[] getSshPublicKeys() {
    return sshPublicKeys;
  }

  public void setSshPublicKeys(String[] sshPublicKeys) {
    this.sshPublicKeys = sshPublicKeys;
  }

  public HashMap<String, String> getCustomTags() {
    return customTags;
  }

  public void setCustomTags(HashMap<String, String> customTags) {
    this.customTags = customTags;
  }

  public String getClusterLogConf() {
    return clusterLogConf;
  }

  public void setClusterLogConf(String clusterLogConf) {
    this.clusterLogConf = clusterLogConf;
  }

  public HashMap<String, String> getSparkEnvironmentVariables() {
    return sparkEnvironmentVariables;
  }

  public void setSparkEnvironmentVariables(HashMap<String, String> sparkEnvironmentVariables) {
    this.sparkEnvironmentVariables = sparkEnvironmentVariables;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public Date getLastModificationDate() {
    return lastModificationDate;
  }

  public void setLastModificationDate(Date lastModificationDate) {
    this.lastModificationDate = lastModificationDate;
  }

  public Boolean getIsCached() {
    return isCached;
  }

  public void setIsCached(Boolean isCached) {
    this.isCached = isCached;
  }

}
