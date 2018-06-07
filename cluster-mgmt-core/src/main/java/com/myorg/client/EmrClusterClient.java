package com.myorg.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.model.AmazonElasticMapReduceException;
import com.amazonaws.services.elasticmapreduce.model.Cluster;
import com.amazonaws.services.elasticmapreduce.model.ClusterStatus;
import com.amazonaws.services.elasticmapreduce.model.DescribeClusterRequest;
import com.amazonaws.services.elasticmapreduce.model.DescribeClusterResult;
import com.amazonaws.services.elasticmapreduce.model.Ec2InstanceAttributes;
import com.amazonaws.services.elasticmapreduce.model.Instance;
import com.amazonaws.services.elasticmapreduce.model.InstanceGroup;
import com.amazonaws.services.elasticmapreduce.model.InstanceGroupConfig;
import com.amazonaws.services.elasticmapreduce.model.InstanceRoleType;
import com.amazonaws.services.elasticmapreduce.model.JobFlowInstancesConfig;
import com.amazonaws.services.elasticmapreduce.model.ListInstanceGroupsRequest;
import com.amazonaws.services.elasticmapreduce.model.ListInstanceGroupsResult;
import com.amazonaws.services.elasticmapreduce.model.MarketType;
import com.amazonaws.services.elasticmapreduce.model.RunJobFlowRequest;
import com.amazonaws.services.elasticmapreduce.model.RunJobFlowResult;
import com.amazonaws.services.elasticmapreduce.model.StepConfig;
import com.amazonaws.services.elasticmapreduce.model.Tag;
import com.amazonaws.services.elasticmapreduce.model.TerminateJobFlowsRequest;
import com.myorg.dto.AwsAttributesDTO;
import com.myorg.dto.ClusterInfoDTO;
import com.myorg.dto.EMRStep;
import com.myorg.dto.ResizeClusterDTO;
import com.myorg.dto.SparkNodeDTO;
import com.myorg.entity.ClusterInfo;
import com.myorg.error.AppErrorCode;
import com.myorg.error.ClusterManagementException;
import com.myorg.error.ErrorMessage;
import com.myorg.util.Constants;

@org.springframework.context.annotation.Configuration
public class EmrClusterClient extends EmrClusterClientUtil implements ClusterClient {

  private String accessKey;
  private String secretKey;

  private static final Logger LOG = LoggerFactory.getLogger(EmrClusterClient.class);

  public EmrClusterClient() {
    loadEmrCredentials();
  }

  private void loadEmrCredentials() {
    // Fallback Credentials, if we didn't receive creds in request
    InputStream resourceStream =
        this.getClass().getClassLoader().getResourceAsStream(Constants.APPLICATION_PROPERTIES);
    if (resourceStream == null) {
      throw new IllegalArgumentException("Resource Not Found: " + Constants.APPLICATION_PROPERTIES);
    }
    Properties props = new Properties();
    try {
      props.load(resourceStream);
    } catch (IOException e) {
      throw new IllegalArgumentException(
          "Unable to load Resource: " + Constants.APPLICATION_PROPERTIES);
    }
    this.accessKey = props.getProperty(Constants.Emr.ACCESS_KEY);
    this.secretKey = props.getProperty(Constants.Emr.SECRET_KEY);
  }

  @Override
  public ClusterInfo createCluster(ClusterInfoDTO clusterInfoDTO) throws IOException {
    try {
      String accessKey = clusterInfoDTO.awsAttributes.accessKey;
      String secretKey = clusterInfoDTO.awsAttributes.secret;
      String region = clusterInfoDTO.awsAttributes.region;
      AmazonElasticMapReduce emr;
      LOG.info("Acquiring EMR Client");
      emr = getEmrClient(accessKey, secretKey, region);

      String clusterName = clusterInfoDTO.clusterName;
      // This needs to derived from multiple fields, better to have toUri method in the object
      // itself
      String logUri = clusterInfoDTO.clusterLogConf.s3.destination;
      List<Tag> tags = getTags(clusterInfoDTO.customTags);

      String ec2KeyName = Constants.Emr.EC2_KEY;
      String emrRole = Constants.Emr.SERVICE_ROLE;
      String ec2Role = Constants.Emr.JOB_FLOW_ROLE;
      String masterInstanceType = clusterInfoDTO.driverNodeTypeId;
      String slaveInstanceType = clusterInfoDTO.nodeTypeId;
      String masterBidPrice = Constants.Emr.MASTER_BID_PRICE;
      String slaveBidPrice = Constants.Emr.SLAVE_BID_PRICE;
      String emrManagedMasterSecurityGroup = clusterInfoDTO.awsAttributes.emrMasterSecurityGroup;
      String emrManagedSlaveSecurityGroup = clusterInfoDTO.awsAttributes.emrSlaveSecurityGroup;
      // Tells us how many nodes to be launched with onDemand Market
      // If 0, every thing spot
      // If 1, Only master is on OnDemand
      // If > 1, master is onDemand and (count -1) slaves on OnDemand
      int onDemandNodes = 0;
      int numSlaveNodes = clusterInfoDTO.numWorkers;

      if (clusterInfoDTO.awsAttributes != null) {
        if (clusterInfoDTO.awsAttributes.masterBidPrice != null) {
          masterBidPrice = clusterInfoDTO.awsAttributes.masterBidPrice.toString();
        }
        if (clusterInfoDTO.awsAttributes.slaveBidPrice != null) {
          slaveBidPrice = clusterInfoDTO.awsAttributes.slaveBidPrice.toString();
        }
        if (clusterInfoDTO.awsAttributes.firstOnDemand != null) {
          onDemandNodes = clusterInfoDTO.awsAttributes.firstOnDemand;
        }
        if (StringUtils.isNotBlank(clusterInfoDTO.awsAttributes.privateKeyName)) {
          ec2KeyName = clusterInfoDTO.awsAttributes.privateKeyName;
        }
        if (StringUtils.isNotBlank(clusterInfoDTO.awsAttributes.emrRole)) {
          emrRole = clusterInfoDTO.awsAttributes.emrRole;
        }
        if (StringUtils.isNotBlank(clusterInfoDTO.awsAttributes.instanceProfileArn)) {
          ec2Role = clusterInfoDTO.awsAttributes.instanceProfileArn;
        }
      }

      InstanceGroupConfig masterGroup =
          new InstanceGroupConfig(InstanceRoleType.MASTER, masterInstanceType, 1);

      if (onDemandNodes > 0) {
        masterGroup = masterGroup.withMarket(MarketType.ON_DEMAND);
      } else {
        masterGroup = masterGroup.withMarket(MarketType.SPOT).withBidPrice(masterBidPrice);
      }

      List<InstanceGroupConfig> slaveGroups =
          getSlaveGroups(slaveInstanceType, numSlaveNodes, onDemandNodes, slaveBidPrice);
      Collection<InstanceGroupConfig> instanceGroups = new ArrayList<>();

      instanceGroups.add(masterGroup);
      instanceGroups.addAll(slaveGroups);

      // Need to get that from cluster info based on cluster is permanent or automated
      int autoTerminateMin = clusterInfoDTO.autoTerminationMinutes;
      boolean keepJobFlowAliveWhenNoSteps = autoTerminateMin == 0;

      JobFlowInstancesConfig instances = new JobFlowInstancesConfig().withEc2KeyName(ec2KeyName)
          .withKeepJobFlowAliveWhenNoSteps(keepJobFlowAliveWhenNoSteps)
          .withInstanceGroups(instanceGroups).withTerminationProtected(false);

      if (emrManagedMasterSecurityGroup != null) {
        instances.setEmrManagedMasterSecurityGroup(emrManagedMasterSecurityGroup);
      }
      if (emrManagedSlaveSecurityGroup != null) {
        instances.setEmrManagedSlaveSecurityGroup(emrManagedSlaveSecurityGroup);
      }

      ArrayList<EMRStep> steps = clusterInfoDTO.awsAttributes.emrSteps;

      RunJobFlowRequest request = new RunJobFlowRequest().withName(clusterName)
          .withReleaseLabel(Constants.Emr.RELEASE_LABEL).withLogUri(logUri).withServiceRole(emrRole)
          .withJobFlowRole(ec2Role).withApplications(Constants.Emr.getApplications())
          .withVisibleToAllUsers(true).withInstances(instances).withTags(tags);


      if (steps != null && steps.size() > 0) {
        List<StepConfig> stepConfigs = new ArrayList<>();
        LOG.info("Adding Steps to the EMR Cluster Create Request");
        for (EMRStep step : steps) {
          StepConfig stepConfig = getStepConfig(step);
          if (stepConfig != null) {
            stepConfigs.add(stepConfig);
          }
        }

        request = request.withSteps(stepConfigs);
      }

      if (clusterInfoDTO.autoscale != null)
        enableEmrAutoScaling(clusterInfoDTO, emr);

      RunJobFlowResult result = emr.runJobFlow(request);
      String clusterId = result.getJobFlowId();
      LOG.info("Cluster with id : {} created for {}", clusterId, clusterName);
      ClusterInfo clusterInfo = new ClusterInfo();
      // Todo Encrypting sensitive information, before persisting into DB
      clusterInfo.setClusterId(clusterId);
      clusterInfo.setClusterName(clusterName);
      Date creationDate = new Date();
      clusterInfo.setCreationDate(creationDate);
      clusterInfo.setLastModificationDate(creationDate);
      clusterInfo.setIsCached(false);
      clusterInfo.setNumWorkers(numSlaveNodes);
      clusterInfo.setAutoTerminationMinutes(autoTerminateMin);
      clusterInfo.setDriverNodeTypeId(masterInstanceType);
      clusterInfo.setNodeTypeId(slaveInstanceType);
      clusterInfo.setClusterLogConf(logUri);

      AwsAttributesDTO awsAttributes = new AwsAttributesDTO();

      if (StringUtils.isNotBlank(accessKey) && StringUtils.isNotBlank(secretKey)) {
        awsAttributes.accessKey = accessKey;
        awsAttributes.secret = secretKey;
      }

      awsAttributes.firstOnDemand = onDemandNodes;
      awsAttributes.emrRole = emrRole;
      awsAttributes.instanceProfileArn = ec2Role;
      awsAttributes.privateKeyName = ec2KeyName;
      awsAttributes.masterBidPrice = Double.valueOf(masterBidPrice);
      awsAttributes.slaveBidPrice = Double.valueOf(slaveBidPrice);
      awsAttributes.emrSteps = steps;
      awsAttributes.region = region;
      awsAttributes.emrMasterSecurityGroup = emrManagedMasterSecurityGroup;
      awsAttributes.emrSlaveSecurityGroup = emrManagedSlaveSecurityGroup;

      clusterInfo.setAwsAttributes(awsAttributes);

      return clusterInfo;
    } catch (AmazonElasticMapReduceException e) {
      ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
          AppErrorCode.EMR_SERVICE_ERROR, "Cluster create failed for reason :" + e.getMessage(),
          Arrays.toString(e.getStackTrace()));
      throw new ClusterManagementException(errorMessage, e);
    } catch (Exception e) {
      ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
          AppErrorCode.EMR_CLIENT_ERROR, "Cluster create failed for reason :" + e.getMessage(),
          Arrays.toString(e.getStackTrace()));
      throw new ClusterManagementException(errorMessage, e);
    }

  }

  @Override
  public ClusterInfo getCluster(ClusterInfo cluster) throws IOException {
    try {
      String clusterId = cluster.getClusterId();

      AmazonElasticMapReduce emr;
      AwsAttributesDTO awsAttributes = getAwsAttributes(cluster.getAwsAttributes());

      String accesskey = awsAttributes.accessKey;
      String secretKey = awsAttributes.secret;
      String region = awsAttributes.region;
      LOG.info("Acquiring EMR Client");
      emr = getEmrClient(accesskey, secretKey, region);

      DescribeClusterRequest request = new DescribeClusterRequest().withClusterId(clusterId);
      DescribeClusterResult result = emr.describeCluster(request);
      ClusterInfo clusterInfo = new ClusterInfo();
      Cluster awsCluster = result.getCluster();

      LOG.info("Getting EC2 Attributes from Cluster : {}, {}", clusterId, cluster.getClusterName());
      Ec2InstanceAttributes ec2Attributes = awsCluster.getEc2InstanceAttributes();

      String emrRole = awsCluster.getServiceRole();
      String ec2KeyName = ec2Attributes.getEc2KeyName();
      awsAttributes.zoneId = ec2Attributes.getEc2AvailabilityZone();
      awsAttributes.instanceProfileArn = ec2Attributes.getIamInstanceProfile();
      awsAttributes.emrRole = emrRole;
      awsAttributes.privateKeyName = ec2KeyName;
      awsAttributes.emrMasterSecurityGroup = ec2Attributes.getEmrManagedMasterSecurityGroup();
      awsAttributes.emrSlaveSecurityGroup = ec2Attributes.getEmrManagedSlaveSecurityGroup();

      LOG.info("Getting EC2 Instance Configs from Cluster : {}, {}", clusterId,
          cluster.getClusterName());
      ListInstanceGroupsRequest req = new ListInstanceGroupsRequest().withClusterId(clusterId);
      ListInstanceGroupsResult res = emr.listInstanceGroups(req);
      SparkNodeDTO driver = new SparkNodeDTO();
      List<InstanceGroup> groups = res.getInstanceGroups();
      List<SparkNodeDTO> executors = new ArrayList<>();
      int numWorkers = 0;
      String nodeType = null;
      String driverNodeType = null;
      for (InstanceGroup instanceGroup : groups) {
        if (StringUtils.equalsIgnoreCase(instanceGroup.getInstanceGroupType(),
            InstanceRoleType.CORE.toString())) {
          nodeType = instanceGroup.getInstanceType();
          numWorkers = Math.max(instanceGroup.getRunningInstanceCount(),
              instanceGroup.getRequestedInstanceCount());
          List<Instance> instances =
              getInstancesFromInstanceGroup(emr, instanceGroup.getId(), clusterId);
          for (Instance instance : instances) {
            executors.add(getNode(instance));
          }
        } else {
          driverNodeType = instanceGroup.getInstanceType();
          List<Instance> instances =
              getInstancesFromInstanceGroup(emr, instanceGroup.getId(), clusterId);
          for (Instance instance : instances) {
            driver = getNode(instance);
          }
        }
      }

      HashMap<String, String> customTags = new HashMap<>();

      List<Tag> tags = awsCluster.getTags();
      for (Tag tag : tags) {
        customTags.put(tag.getKey(), tag.getValue());
      }

      String clusterLogConf = awsCluster.getLogUri();

      ClusterStatus status = awsCluster.getStatus();

      // need a method for converting this
      clusterInfo.setClusterId(awsCluster.getId());
      clusterInfo.setClusterName(awsCluster.getName());
      clusterInfo.setNumWorkers(numWorkers);
      clusterInfo.setDriver(driver);
      clusterInfo.setNodeTypeId(nodeType);
      clusterInfo.setDriverNodeTypeId(driverNodeType);
      clusterInfo.setAwsAttributes(awsAttributes);
      // All the tags at one place
      clusterInfo.setCustomTags(customTags);
      // storing the log uri in log conf
      clusterInfo.setClusterLogConf(clusterLogConf);
      clusterInfo.setState(status.getState());
      clusterInfo.setStateMessage(status.getStateChangeReason().getMessage());
      clusterInfo.setStartTime(status.getTimeline().getCreationDateTime());
      clusterInfo.setTerminatedTime(status.getTimeline().getEndDateTime());
      clusterInfo.setIsCached(false);
      clusterInfo.setLastModificationDate(new Date());
      clusterInfo.setCreationDate(cluster.getCreationDate());

      return clusterInfo;
    } catch (AmazonElasticMapReduceException e) {
      ErrorMessage errorMessage =
          new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), AppErrorCode.EMR_SERVICE_ERROR,
              "Cluster details fetch failed for reason :" + e.getMessage(),
              Arrays.toString(e.getStackTrace()));
      throw new ClusterManagementException(errorMessage, e);
    } catch (Exception e) {
      ErrorMessage errorMessage =
          new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), AppErrorCode.EMR_CLIENT_ERROR,
              "Cluster details fetch failed for reason :" + e.getMessage(),
              Arrays.toString(e.getStackTrace()));
      throw new ClusterManagementException(errorMessage, e);
    }
  }

  @Override
  public String terminateCluster(ClusterInfo cluster) throws IOException {
    try {
      AmazonElasticMapReduce emr;
      AwsAttributesDTO awsAttributes = getAwsAttributes(cluster.getAwsAttributes());

      String accesskey = awsAttributes.accessKey;
      String secretKey = awsAttributes.secret;
      String region = awsAttributes.region;
      LOG.info("Acquiring EMR Client");
      emr = getEmrClient(accesskey, secretKey, region);

      TerminateJobFlowsRequest terminateJobFlowsRequest =
          new TerminateJobFlowsRequest().withJobFlowIds(cluster.getClusterId());
      emr.terminateJobFlows(terminateJobFlowsRequest);
      LOG.info("Terminated EMR Cluster : {}, {}", cluster.getClusterId(), cluster.getClusterName());
      return cluster.getClusterId();
    } catch (AmazonElasticMapReduceException e) {
      ErrorMessage errorMessage =
          new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), AppErrorCode.EMR_SERVICE_ERROR,
              "Cluster Termination failed for reason :" + e.getMessage(),
              Arrays.toString(e.getStackTrace()));
      throw new ClusterManagementException(errorMessage, e);
    } catch (Exception e) {
      ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
          AppErrorCode.EMR_CLIENT_ERROR, "Cluster Termination failed for reason :" + e.getMessage(),
          Arrays.toString(e.getStackTrace()));
      throw new ClusterManagementException(errorMessage, e);
    }
  }

  @Override
  public String startCluster(String clusterId) throws IOException {
    return null;
  }

  @Override
  public String restartCluster(String clusterId) throws IOException {
    return null;
  }

  @Override
  public String resizeCluster(ResizeClusterDTO resizeClusterDTO) throws IOException {
    return null;
  }

}
