package com.myorg.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder;
import com.amazonaws.services.elasticmapreduce.model.AdjustmentType;
import com.amazonaws.services.elasticmapreduce.model.AutoScalingPolicy;
import com.amazonaws.services.elasticmapreduce.model.CloudWatchAlarmDefinition;
import com.amazonaws.services.elasticmapreduce.model.ComparisonOperator;
import com.amazonaws.services.elasticmapreduce.model.Instance;
import com.amazonaws.services.elasticmapreduce.model.InstanceGroupConfig;
import com.amazonaws.services.elasticmapreduce.model.InstanceRoleType;
import com.amazonaws.services.elasticmapreduce.model.ListInstancesRequest;
import com.amazonaws.services.elasticmapreduce.model.ListInstancesResult;
import com.amazonaws.services.elasticmapreduce.model.MarketType;
import com.amazonaws.services.elasticmapreduce.model.PutAutoScalingPolicyRequest;
import com.amazonaws.services.elasticmapreduce.model.ScalingAction;
import com.amazonaws.services.elasticmapreduce.model.ScalingConstraints;
import com.amazonaws.services.elasticmapreduce.model.ScalingRule;
import com.amazonaws.services.elasticmapreduce.model.ScalingTrigger;
import com.amazonaws.services.elasticmapreduce.model.SimpleScalingPolicyConfiguration;
import com.amazonaws.services.elasticmapreduce.model.Statistic;
import com.amazonaws.services.elasticmapreduce.model.StepConfig;
import com.amazonaws.services.elasticmapreduce.model.Tag;
import com.amazonaws.services.elasticmapreduce.model.Unit;
import com.amazonaws.services.elasticmapreduce.util.StepFactory;
import com.myorg.dto.AwsAttributesDTO;
import com.myorg.dto.ClusterInfoDTO;
import com.myorg.dto.EMRStep;
import com.myorg.dto.SparkNodeDTO;
import com.myorg.util.Constants;

public abstract class EmrClusterClientUtil {

  private static final Logger LOG = LoggerFactory.getLogger(EmrClusterClientUtil.class);

  protected AmazonElasticMapReduce getEmrClient(final String accessKey, final String secretKey,
      final String region) {
    boolean isAccesskeyPresent = StringUtils.isNotBlank(accessKey);
    boolean isSecretPresent = StringUtils.isNotBlank(secretKey);
    boolean isRegionPresent = StringUtils.isNotBlank(region);
    AWSCredentials creds;

    if (isAccesskeyPresent && isSecretPresent) {
      creds = new BasicAWSCredentials(accessKey, secretKey);
    } else {
      LOG.info("Credentials are present , Using fallback Credentials to Create a EMR Client");
      creds = new BasicAWSCredentials(accessKey, secretKey);
    }

    if (isRegionPresent) {
      return AmazonElasticMapReduceClientBuilder.standard().withRegion(region)
          .withCredentials(new AWSStaticCredentialsProvider(creds)).build();
    } else {
      return AmazonElasticMapReduceClientBuilder.standard()
          .withCredentials(new AWSStaticCredentialsProvider(creds)).build();
    }
  }

  protected StepConfig getStepConfig(EMRStep step) {
    String name = step.name;
    String type = step.type;
    String action = step.actionOnFailure;
    String location = step.location;
    ArrayList<String> arguments = step.arguments;
    String[] args = new String[0];
    if (arguments != null && arguments.size() > 0) {
      args = new String[arguments.size()];
      arguments.toArray(args);
    }

    // other implementations can be added later
    switch (type) {
      case Constants.Emr.StepType.HIVE:
        return new StepConfig().withName(name).withActionOnFailure(action)
            .withHadoopJarStep(new StepFactory().newRunHiveScriptStep(location, args));
    }
    return null;

  }

  protected List<Tag> getTags(Map<String, String> customTags) {
    List<Tag> tags = new ArrayList<>();
    if (customTags != null) {
      for (Entry<String, String> entry : customTags.entrySet()) {
        tags.add(new Tag().withKey(entry.getKey()).withValue(entry.getValue()));
      }
    } else {
      tags.add(new Tag().withKey(Constants.Emr.DEFAULT_TAG_KEY)
          .withValue(Constants.Emr.DEFAULT_TAG_VALUE));
    }
    return tags;
  }

  protected List<InstanceGroupConfig> getSlaveGroups(String slaveInstanceType, int numSlaveNodes,
      int onDemandNodes, String slaveBidPrice) {
    List<InstanceGroupConfig> slaveGroups = new ArrayList<>();

    if (onDemandNodes > 1) {
      int slaveOnDemand = numSlaveNodes - (Math.min(onDemandNodes, numSlaveNodes) - 1);
      int slaveSpot = numSlaveNodes - slaveOnDemand;

      if (slaveOnDemand > 0) {
        InstanceGroupConfig slaveGroup =
            new InstanceGroupConfig(InstanceRoleType.CORE, slaveInstanceType, slaveOnDemand)
                .withMarket(MarketType.ON_DEMAND);
        slaveGroups.add(slaveGroup);
      }

      if (slaveSpot > 0) {
        InstanceGroupConfig slaveGroup =
            new InstanceGroupConfig(InstanceRoleType.CORE, slaveInstanceType, slaveSpot)
                .withBidPrice(slaveBidPrice).withMarket(MarketType.SPOT);
        slaveGroups.add(slaveGroup);
      }

    } else {
      InstanceGroupConfig slaveGroup =
          new InstanceGroupConfig(InstanceRoleType.CORE, slaveInstanceType, numSlaveNodes)
              .withBidPrice(slaveBidPrice).withMarket(MarketType.SPOT);
      slaveGroups.add(slaveGroup);
    }

    return slaveGroups;
  }

  protected AwsAttributesDTO getAwsAttributes(AwsAttributesDTO awsAttributes) {
    if (awsAttributes != null) {
      AwsAttributesDTO awsAttributesDTO = new AwsAttributesDTO();
      // Implementing clone method would be better.
      awsAttributesDTO.accessKey = awsAttributes.accessKey;
      awsAttributesDTO.secret = awsAttributes.secret;
      awsAttributesDTO.emrResizeRole = awsAttributes.emrResizeRole;
      awsAttributesDTO.emrRole = awsAttributes.emrRole;
      awsAttributesDTO.firstOnDemand = awsAttributes.firstOnDemand;
      awsAttributesDTO.instanceProfileArn = awsAttributes.instanceProfileArn;
      awsAttributesDTO.privateKeyName = awsAttributes.privateKeyName;
      awsAttributesDTO.masterBidPrice = awsAttributes.masterBidPrice;
      awsAttributesDTO.slaveBidPrice = awsAttributes.slaveBidPrice;
      awsAttributesDTO.zoneId = awsAttributes.zoneId;
      awsAttributesDTO.emrSteps = awsAttributes.emrSteps;
      awsAttributesDTO.region = awsAttributes.region;
      awsAttributesDTO.emrMasterSecurityGroup = awsAttributes.emrMasterSecurityGroup;
      awsAttributesDTO.emrSlaveSecurityGroup = awsAttributes.emrSlaveSecurityGroup;

      return awsAttributesDTO;
    }

    return null;

  }

  protected SparkNodeDTO getNode(Instance instance) {
    SparkNodeDTO node = new SparkNodeDTO();
    node.privateIp = instance.getPrivateIpAddress();
    node.publicDns = instance.getPublicDnsName();
    node.nodeId = instance.getId();
    node.instanceId = instance.getEc2InstanceId();
    // Do we include other timestamp values also..?
    node.startTimestamp = instance.getStatus().getTimeline().getReadyDateTime();
    return node;
  }

  protected List<Instance> getInstancesFromInstanceGroup(AmazonElasticMapReduce emr, String id,
      String clusterId) {
    List<Instance> instances = new ArrayList<>();
    ListInstancesRequest listInstancesRequest =
        new ListInstancesRequest().withInstanceGroupId(id).withClusterId(clusterId);
    ListInstancesResult listInstancesResult = emr.listInstances(listInstancesRequest);
    instances = listInstancesResult.getInstances();
    return instances;
  }

  protected AmazonElasticMapReduce enableEmrAutoScaling(final ClusterInfoDTO clusterInfoDTO,
      final AmazonElasticMapReduce emr) {

    final PutAutoScalingPolicyRequest autoScalePolicyReq = new PutAutoScalingPolicyRequest();
    // TODO : Remove cluster id hard coding.
    autoScalePolicyReq.setClusterId("clusterId");

    final AutoScalingPolicy autoScalePolicy = new AutoScalingPolicy();

    final ScalingConstraints constraints = new ScalingConstraints();
    constraints.setMaxCapacity(clusterInfoDTO.autoscale.maxWorkers);
    constraints.setMinCapacity(clusterInfoDTO.autoscale.minWorkers);
    autoScalePolicy.setConstraints(constraints);

    final List<ScalingRule> rules = new ArrayList<ScalingRule>();
    final ScalingRule rule = new ScalingRule();
    rule.setName("YARNMemoryAvailablePercentage");
    rule.setDescription("Rule to test the yarn memory avaibility");

    final ScalingAction action = new ScalingAction();
    final SimpleScalingPolicyConfiguration policy = new SimpleScalingPolicyConfiguration();
    policy.setAdjustmentType(AdjustmentType.CHANGE_IN_CAPACITY);
    policy.setCoolDown(300);
    policy.setScalingAdjustment(-10);
    action.setSimpleScalingPolicyConfiguration(policy);
    rule.setAction(action);

    final ScalingTrigger trigger = new ScalingTrigger();
    final CloudWatchAlarmDefinition alarmDef = new CloudWatchAlarmDefinition();
    alarmDef.setNamespace("YARN Memory Available Percentage");
    alarmDef.setMetricName("YARNMemoryAvailablePercentage");
    alarmDef.setComparisonOperator(ComparisonOperator.GREATER_THAN);
    alarmDef.setThreshold(40.0);
    alarmDef.setEvaluationPeriods(1);
    alarmDef.setStatistic(Statistic.AVERAGE);
    alarmDef.setUnit(Unit.PERCENT);
    alarmDef.setPeriod(300);

    trigger.setCloudWatchAlarmDefinition(alarmDef);
    rule.setTrigger(trigger);
    rule.setAction(action);

    rules.add(rule);
    autoScalePolicy.setRules(rules);
    autoScalePolicyReq.setAutoScalingPolicy(autoScalePolicy);
    emr.putAutoScalingPolicy(autoScalePolicyReq);
    return emr;
  }

}
