package com.myorg.databricks.cluster.builder;

import com.myorg.databricks.client.ClustersClient;
import com.myorg.databricks.client.HttpException;
import com.myorg.databricks.client.entities.clusters.AutoScaleDTO;
import com.myorg.databricks.client.entities.clusters.ClusterInfoDTO;
import com.myorg.databricks.cluster.ClusterConfigException;
import com.myorg.databricks.cluster.InteractiveCluster;
import com.myorg.databricks.cluster.AwsAttribute.AwsAvailability;
import com.myorg.databricks.cluster.AwsAttribute.EbsVolumeType;
import com.myorg.databricks.library.*;
import java.util.ArrayList;

public class InteractiveClusterBuilder extends ClusterBuilder {
    protected ClustersClient _client;
    private String _clusterName;
    private Integer _numWorkers;
    private Integer _autoscaleMinWorkers;
    private Integer _autoscaleMaxWorkers;
    private Integer _autoTerminationMinutes;
    private ArrayList<Library> _libraries = new ArrayList<Library>();

    public InteractiveClusterBuilder(ClustersClient client, String clusterName, Integer numWorkers) {
        _client = client;
        _clusterName = clusterName;
        _numWorkers = numWorkers;
    }

    public InteractiveClusterBuilder(ClustersClient client, String clusterName, Integer minWorkers, Integer maxWorkers) {
        _client = client;
        _clusterName = clusterName;
        _autoscaleMinWorkers = minWorkers;
        _autoscaleMaxWorkers = maxWorkers;
    }

    @Override
    protected ClusterInfoDTO applySettings(ClusterInfoDTO clusterInfoDTO) {
        clusterInfoDTO = super.applySettings(clusterInfoDTO);

        clusterInfoDTO.ClusterName = _clusterName;
        clusterInfoDTO.NumWorkers = _numWorkers;
        clusterInfoDTO.AutoTerminationMinutes = _autoTerminationMinutes;

        if(_autoscaleMinWorkers != null && _autoscaleMaxWorkers != null) {
            AutoScaleDTO autoScaleDTO = new AutoScaleDTO();
            autoScaleDTO.MinWorkers = _autoscaleMinWorkers;
            autoScaleDTO.MaxWorkers = _autoscaleMaxWorkers;
            clusterInfoDTO.AutoScale = autoScaleDTO;
        }
        return clusterInfoDTO;
    }

    @Override
    public InteractiveClusterBuilder withSparkVersion(String sparkVersion) {
        return (InteractiveClusterBuilder)super.withSparkVersion(sparkVersion);
    }

    @Override
    public InteractiveClusterBuilder withNodeType(String nodeTypeId) {
        return (InteractiveClusterBuilder)super.withNodeType(nodeTypeId);
    }

    @Override
    public InteractiveClusterBuilder withDriverNodeType(String nodeTypeId) {
        return (InteractiveClusterBuilder)super.withDriverNodeType(nodeTypeId);
    }

    @Override
    public InteractiveClusterBuilder withAwsFirstOnDemand(Integer onDemandInstances) {
        return (InteractiveClusterBuilder)super.withAwsFirstOnDemand(onDemandInstances);
    }

    @Override
    public InteractiveClusterBuilder withAwsAvailability(AwsAvailability availability) {
        return (InteractiveClusterBuilder)super.withAwsAvailability(availability);
    }

    @Override
    public InteractiveClusterBuilder withAwsZone(String zoneId) {
        return (InteractiveClusterBuilder)super.withAwsZone(zoneId);
    }

    @Override
    public InteractiveClusterBuilder withAwsInstanceProfileArn(String instanceProfileArn) {
        return (InteractiveClusterBuilder)super.withAwsInstanceProfileArn(instanceProfileArn);
    }

    @Override
    public InteractiveClusterBuilder withAwsSpotBidPricePercent(Integer spotBidPricePercent) {
        return (InteractiveClusterBuilder)super.withAwsSpotBidPricePercent(spotBidPricePercent);
    }

    @Override
    public InteractiveClusterBuilder withAwsEbsVolume(EbsVolumeType type,
                                              Integer count,
                                              Integer size) {
        return (InteractiveClusterBuilder)super.withAwsEbsVolume(type, count, size);
    }

    @Override
    public InteractiveClusterBuilder withElasticDisk() {
        return (InteractiveClusterBuilder)super.withElasticDisk();
    }

    @Override
    public InteractiveClusterBuilder withSparkConf(String key, String value){
        return (InteractiveClusterBuilder)super.withSparkConf(key, value);
    }

    @Override
    public InteractiveClusterBuilder withSshPublicKey(String publicKey){
        return (InteractiveClusterBuilder)super.withSshPublicKey(publicKey);
    }

    @Override
    public InteractiveClusterBuilder withCustomTag(String key, String value) {
        return (InteractiveClusterBuilder)super.withCustomTag(key, value);
    }

    @Override
    public InteractiveClusterBuilder withDbfsLogConf(String destination) {
        return (InteractiveClusterBuilder)super.withDbfsLogConf(destination);
    }

    @Override
    public InteractiveClusterBuilder withS3LogConf(String destination,
                                           String region,
                                           String endpoint) {
        return (InteractiveClusterBuilder) super.withS3LogConf(destination, region, endpoint);
    }

    @Override
    public InteractiveClusterBuilder withS3LogConfEncryption(String encryptionType,
                                                     String kmsKey,
                                                     String cannedAcl) {
        return (InteractiveClusterBuilder)super.withS3LogConfEncryption(encryptionType, kmsKey, cannedAcl);
    }

    @Override
    public InteractiveClusterBuilder withSparkEnvironmentVariable(String key, String value) {
        return (InteractiveClusterBuilder)super.withSparkEnvironmentVariable(key, value);
    }


    public InteractiveClusterBuilder withAutoTerminationMinutes(Integer minutes) {
        _autoTerminationMinutes = minutes;
        return this;
    }

    public InteractiveClusterBuilder withLibrary(Library library) {
        _libraries.add(library);
        return this;
    }

    public InteractiveCluster create() throws ClusterConfigException, HttpException {
        validateLogConf();

        ClusterInfoDTO clusterInfoDTO = new ClusterInfoDTO();
        clusterInfoDTO = applySettings(clusterInfoDTO);

        //create cluster via client
        clusterInfoDTO.ClusterId = _client.create(clusterInfoDTO);

        InteractiveCluster cluster = new InteractiveCluster(_client, clusterInfoDTO);

        //install libraries
        for (Library library : _libraries) {
            if(library instanceof JarLibrary) {
                cluster.installLibrary((JarLibrary) library);
            } else if (library instanceof EggLibrary) {
                cluster.installLibrary((EggLibrary) library);
            } else if (library instanceof MavenLibrary) {
                cluster.installLibrary((MavenLibrary) library);
            } else if (library instanceof PyPiLibrary) {
                cluster.installLibrary((PyPiLibrary) library);
            } else if (library instanceof CranLibrary) {
                cluster.installLibrary((CranLibrary) library);
            }
        }

        return cluster;
    }

}
