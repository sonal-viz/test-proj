package com.myorg.service;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myorg.client.ClusterClient;
import com.myorg.client.DatabricksClusterClient;
import com.myorg.client.EmrClusterClient;
import com.myorg.dto.AutoScaleDTO;
import com.myorg.dto.ClusterInfoDTO;
import com.myorg.dto.ResizeClusterDTO;
import com.myorg.entity.ClusterInfo;
import com.myorg.error.ClusterManagementException;
import com.myorg.mapper.ClusterInfoMapper;
import com.myorg.repository.ClusterRepository;
import com.myorg.util.ClusterPlatform;
import com.myorg.validation.ClusterServiceRequestValidator;
import com.myorg.databricks.cluster.ClusterConfigException;

@Service
public class ClusterServiceImpl implements IClusterService {

  @Autowired
  ClusterRepository clusterRepository;

  @Autowired
  DatabricksClusterClient databricksClusterClient;

  @Autowired
  EmrClusterClient emrClusterClient;

  @Autowired
  ClusterServiceRequestValidator clusterServiceRequestValidator;

  private static final Logger LOG = LoggerFactory.getLogger(ClusterServiceImpl.class);

  @Override
  public ClusterInfoDTO createCluster(ClusterInfoDTO clusterInfoDTO) throws IOException {
    try {
      clusterServiceRequestValidator.validateCreateClusterRequest(clusterInfoDTO);
      ClusterClient clusterClient = getPlatformClusterClient(clusterInfoDTO.platform);
      ClusterInfo clusterInfo = clusterClient.createCluster(clusterInfoDTO);

      // persist custom parameters which will not come from remote cluster api.
      clusterInfo.setPlatform(clusterInfoDTO.platform);

      ClusterInfo dbClusterInfo = clusterRepository.save(clusterInfo);
      return ClusterInfoMapper.MAPPER.mapClusterInfoToClusterInfoDTO(dbClusterInfo);
    } catch (ClusterManagementException cme) {
      cme.addMessage("ClusterServiceImpl : error calling createCluster with clusterInfo: "
          + clusterInfoDTO.toString());
      throw cme;
    }
  }

  @Override
  public ClusterInfoDTO getCluster(String clusterId) throws IOException {
    ClusterInfo clusterInfo = clusterRepository.findOne(clusterId);
    ClusterClient clusterClient = getPlatformClusterClient(clusterInfo.getPlatform());
    ClusterInfo updatedClusterInfo = null;
    ClusterInfoDTO clusterInfoDTO;
    try {
      updatedClusterInfo = clusterClient.getCluster(clusterInfo);
   // persist custom parameters which will not come from remote cluster api.
      updatedClusterInfo.setPlatform(clusterInfo.getPlatform());

      clusterRepository.saveAndFlush(updatedClusterInfo);
      
    } catch(Exception e) {
      //need to limit the exceptions
      LOG.error(e.getMessage(),e);
      updatedClusterInfo = clusterInfo;
      updatedClusterInfo.setIsCached(true);
    } finally {
      clusterInfoDTO = ClusterInfoMapper.MAPPER.mapClusterInfoToClusterInfoDTO(updatedClusterInfo);      
      // tbd: Mapper not setting autoscale properly check later
      clusterInfoDTO.autoscale = updatedClusterInfo.getAutoScale();
    }
    
    
    
    
    return clusterInfoDTO;
  }

  @Override
  public String startCluster(String clusterId) throws IOException {
    ClusterInfo clusterInfo = clusterRepository.findOne(clusterId);
    ClusterClient clusterClient = getPlatformClusterClient(clusterInfo.getPlatform());
    return clusterClient.startCluster(clusterId);
  }

  @Override
  public String restartCluster(String clusterId) throws IOException {
    ClusterInfo clusterInfo = clusterRepository.findOne(clusterId);
    ClusterClient clusterClient = getPlatformClusterClient(clusterInfo.getPlatform());
    return clusterClient.restartCluster(clusterId);
  }

  @Override
  public String resizeCluster(ResizeClusterDTO resizeClusterDTO) throws IOException {
    ClusterInfo clusterInfo = clusterRepository.findOne(resizeClusterDTO.clusterId);
    ClusterClient clusterClient = getPlatformClusterClient(clusterInfo.getPlatform());
    return clusterClient.resizeCluster(resizeClusterDTO);
  }

  @Override
  public String terminateCluster(String clusterId) throws IOException {
    ClusterInfo clusterInfo = clusterRepository.findOne(clusterId);
    ClusterClient clusterClient = getPlatformClusterClient(clusterInfo.getPlatform());
    try {
      return clusterClient.terminateCluster(clusterInfo);
    } catch (ClusterManagementException cme) {
      LOG.error(cme.getMessage(), cme);
      throw cme;
    }
  }

  ClusterClient getPlatformClusterClient(ClusterPlatform clusterPlatform) {
    ClusterClient clusterClient = null;
    switch (clusterPlatform) {
      case DATABRICKS:
        clusterClient = databricksClusterClient;
        break;
      case EMR:
        clusterClient = emrClusterClient;
        break;
      default:
        LOG.error("No cluster client found for platform: {}", clusterPlatform);
        break;
    }
    return clusterClient;
  }
}
