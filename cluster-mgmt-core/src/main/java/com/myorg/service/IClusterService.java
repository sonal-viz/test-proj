package com.myorg.service;

import java.io.IOException;

import com.myorg.dto.ClusterInfoDTO;
import com.myorg.dto.ResizeClusterDTO;

public interface IClusterService {

  ClusterInfoDTO createCluster(ClusterInfoDTO clusterInfoDTO) throws IOException;

  ClusterInfoDTO getCluster(String clusterId) throws IOException;

  /**
   * Should start the cluster only if the cluster is in terminated state.
   * 
   * @param clusterId
   * @return
   * @throws IOException
   */
  String startCluster(String clusterId) throws IOException;

  String restartCluster(String clusterId) throws IOException;

  String resizeCluster(ResizeClusterDTO resizeClusterDTO) throws IOException;

  String terminateCluster(String clusterId) throws IOException;

}
