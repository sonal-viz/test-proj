package com.myorg.apptest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import java.io.IOException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.myorg.client.DatabricksClusterClient;
import com.myorg.dto.ClusterInfoDTO;
import com.myorg.entity.ClusterInfo;
import com.myorg.repository.ClusterRepository;
import com.myorg.service.ClusterServiceImpl;
import com.myorg.util.ClusterPlatform;
import com.myorg.validation.ClusterServiceRequestValidator;

@RunWith(MockitoJUnitRunner.class)
public class DatabricksClusterUnitTest {

  @Mock
  ClusterRepository mockClusterRepository;

  @Mock 
  ClusterServiceRequestValidator clusterServiceRequestValidator;

  @Mock
  private DatabricksClusterClient mockDatabricksClusterClient;

  // TODO: only mocks getting injected but Autowired repo is not getting injected.
  @InjectMocks
  private ClusterServiceImpl clusterServiceImpl;

  private static final Logger LOG = LoggerFactory.getLogger(DatabricksClusterUnitTest.class);

  ClusterInfo clusterInfo = new ClusterInfo();
  ClusterInfoDTO clusterInfoDTO = new ClusterInfoDTO();
  ClusterInfoDTO clusterInfoDTO2 = new ClusterInfoDTO();
  static final String clusterId = "XXXX";

  @Before
  public void init() throws IOException {

    clusterInfoDTO.platform = ClusterPlatform.DATABRICKS;
    clusterInfoDTO2.platform = ClusterPlatform.DATABRICKS;

    clusterInfo.setClusterId(clusterId);
    clusterInfo.setClusterName("autoscaling-cluster_demo3");
    clusterInfo.setSparkVersionKey("2.0.x-scala2.10");
    clusterInfo.setNodeTypeId("r3.xlarge");
    clusterInfo.setPlatform(ClusterPlatform.DATABRICKS);
//    clusterInfo.setAutoScale("{\r\n   \"min_workers\": 1,\r\n   \"max_workers\": 2\r\n }");

    try {
      when(mockDatabricksClusterClient.createCluster(clusterInfoDTO)).thenReturn(clusterInfo);
    } catch (IOException e) {
      LOG.error("Exception occured while creating cluster from mock databricks client. Reason: ",
          e);
    }
    // TODO : later instead of mocking databricks client try mocking underlying databricks session
    when(mockDatabricksClusterClient.getCluster(clusterInfo)).thenReturn(clusterInfo);
    when(mockDatabricksClusterClient.createCluster(clusterInfoDTO2)).thenThrow(new IOException());
    when(mockDatabricksClusterClient.startCluster(clusterId)).thenReturn(clusterId);
    when(mockDatabricksClusterClient.restartCluster(clusterId)).thenReturn(clusterId);
    when(mockDatabricksClusterClient.terminateCluster(clusterInfo)).thenReturn(clusterId);

    when(mockClusterRepository.findOne(clusterId)).thenReturn(clusterInfo);
    clusterServiceRequestValidator = new ClusterServiceRequestValidator();

  }

  @Test
  @Ignore
  //Disabling the test temporarily
  public void testCreateClusterOk() throws IOException {
    //String createdClusterId = clusterServiceImpl.createCluster(clusterInfoDTO);
    ClusterInfoDTO dto = clusterServiceImpl.createCluster(clusterInfoDTO);
    assertEquals(clusterId, dto.clusterId);
  }

  @Test(expected = NullPointerException.class)
  public void testCreateClusterNullException() throws IOException {
    clusterServiceImpl.createCluster(null);
  }

  @Test(expected = IOException.class)
  public void testCreateClusterIOException() throws IOException {
    clusterServiceImpl.createCluster(clusterInfoDTO2);
  }

  @Test
  public void testGetClusterOk() throws IOException {
    ClusterInfoDTO clusterInfoDTO = clusterServiceImpl.getCluster(clusterId);
    assertEquals("Cluster names dont match while creating and saving cluster.",
        "autoscaling-cluster_demo3", clusterInfoDTO.clusterName);
  }

  @Test
  public void testStartClusterOk() throws IOException {
    String returnedClusterId = clusterServiceImpl.startCluster(clusterId);
    assertEquals("Cluster ids dont match while starting the cluster.", clusterId,
        returnedClusterId);
  }

  @Test
  public void testRestartClusterOk() throws IOException {
    String returnedClusterId = clusterServiceImpl.restartCluster(clusterId);
    assertEquals("Cluster ids dont match while restarting the cluster.", clusterId,
        returnedClusterId);
  }

  @Test
  public void testTerminateClusterOk() throws IOException {
    String returnedClusterId = clusterServiceImpl.terminateCluster(clusterId);
    assertEquals("Cluster ids dont match while restarting the cluster.", clusterId,
        returnedClusterId);
  }
}
