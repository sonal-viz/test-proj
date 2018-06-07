package com.myorg.databricks.cluster.builder;

import com.myorg.databricks.client.entities.clusters.ClusterInfoDTO;
import com.myorg.databricks.cluster.ClusterConfigException;
import com.myorg.databricks.cluster.AwsAttribute.AwsAvailability;
import com.myorg.databricks.cluster.AwsAttribute.EbsVolumeType;
import com.myorg.databricks.job.builder.AutomatedJobBuilder;
import com.myorg.databricks.job.builder.AutomatedNotebookJobBuilder;

public class AutomatedClusterBuilder extends ClusterBuilder {
    private AutomatedJobBuilder _jobBuilder;
    private int _numWorkers;

    public AutomatedClusterBuilder(AutomatedNotebookJobBuilder jobBuilder, int numWorkers) {
        _jobBuilder = jobBuilder;
        _numWorkers = numWorkers;
    }

    @Override
    protected ClusterInfoDTO applySettings(ClusterInfoDTO clusterInfoDTO) {
        clusterInfoDTO = super.applySettings(clusterInfoDTO);
        clusterInfoDTO.NumWorkers = _numWorkers;

        return clusterInfoDTO;
    }

    @Override
    public AutomatedClusterBuilder withSparkVersion(String sparkVersion) {
        return (AutomatedClusterBuilder)super.withSparkVersion(sparkVersion);
    }

    @Override
    public AutomatedClusterBuilder withNodeType(String nodeTypeId) {
        return (AutomatedClusterBuilder)super.withNodeType(nodeTypeId);
    }

    @Override
    public AutomatedClusterBuilder withDriverNodeType(String nodeTypeId) {
        return (AutomatedClusterBuilder)super.withDriverNodeType(nodeTypeId);
    }

    @Override
    public AutomatedClusterBuilder withAwsFirstOnDemand(Integer onDemandInstances) {
        return (AutomatedClusterBuilder)super.withAwsFirstOnDemand(onDemandInstances);
    }

    @Override
    public AutomatedClusterBuilder withAwsAvailability(AwsAvailability availability) {
        return (AutomatedClusterBuilder)super.withAwsAvailability(availability);
    }

    @Override
    public AutomatedClusterBuilder withAwsZone(String zoneId) {
        return (AutomatedClusterBuilder)super.withAwsZone(zoneId);
    }

    @Override
    public AutomatedClusterBuilder withAwsInstanceProfileArn(String instanceProfileArn) {
        return (AutomatedClusterBuilder)super.withAwsInstanceProfileArn(instanceProfileArn);
    }

    @Override
    public AutomatedClusterBuilder withAwsSpotBidPricePercent(Integer spotBidPricePercent) {
        return (AutomatedClusterBuilder)super.withAwsSpotBidPricePercent(spotBidPricePercent);
    }

    @Override
    public AutomatedClusterBuilder withAwsEbsVolume(EbsVolumeType type,
                                                      Integer count,
                                                      Integer size) {
        return (AutomatedClusterBuilder)super.withAwsEbsVolume(type, count, size);
    }

    @Override
    public AutomatedClusterBuilder withElasticDisk() {
        return (AutomatedClusterBuilder)super.withElasticDisk();
    }

    @Override
    public AutomatedClusterBuilder withSparkConf(String key, String value){
        return (AutomatedClusterBuilder)super.withSparkConf(key, value);
    }

    @Override
    public AutomatedClusterBuilder withSshPublicKey(String publicKey){
        return (AutomatedClusterBuilder)super.withSshPublicKey(publicKey);
    }

    @Override
    public AutomatedClusterBuilder withCustomTag(String key, String value) {
        return (AutomatedClusterBuilder)super.withCustomTag(key, value);
    }

    @Override
    public AutomatedClusterBuilder withDbfsLogConf(String destination) {
        return (AutomatedClusterBuilder)super.withDbfsLogConf(destination);
    }

    @Override
    public AutomatedClusterBuilder withS3LogConf(String destination,
                                                   String region,
                                                   String endpoint) {
        return (AutomatedClusterBuilder) super.withS3LogConf(destination, region, endpoint);
    }

    @Override
    public AutomatedClusterBuilder withS3LogConfEncryption(String encryptionType,
                                                             String kmsKey,
                                                             String cannedAcl) {
        return (AutomatedClusterBuilder)super.withS3LogConfEncryption(encryptionType, kmsKey, cannedAcl);
    }

    @Override
    public AutomatedClusterBuilder withSparkEnvironmentVariable(String key, String value) {
        return (AutomatedClusterBuilder)super.withSparkEnvironmentVariable(key, value);
    }

    public <T extends AutomatedJobBuilder> T addToJob(Class<T> type) throws ClusterConfigException {
        validateLogConf();
        ClusterInfoDTO clusterInfoDTO = new ClusterInfoDTO();
        clusterInfoDTO = applySettings(clusterInfoDTO);
        _jobBuilder.withClusterInfo(clusterInfoDTO);
        return type.cast(_jobBuilder);
    }

}
