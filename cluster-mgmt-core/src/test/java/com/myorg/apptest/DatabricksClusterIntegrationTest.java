package com.myorg.apptest;


import java.io.InputStream;
import java.util.Iterator;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.myorg.databricks.client.DatabricksSession;
import com.myorg.databricks.cluster.ClusterState;
import com.myorg.databricks.cluster.InteractiveCluster;
import com.myorg.databricks.config.DatabricksClientConfiguration;

public class DatabricksClusterIntegrationTest {

  private static final Logger LOG = LoggerFactory.getLogger(DatabricksClusterIntegrationTest.class);

  public static final String CLIENT_CONFIG_RESOURCE_NAME = "application.properties";

  ClassLoader loader = Thread.currentThread().getContextClassLoader();
  InputStream resourceStream = loader.getResourceAsStream(CLIENT_CONFIG_RESOURCE_NAME);
  DatabricksSession _databricks;
  DatabricksClientConfiguration _databricksConfig;

  public DatabricksClusterIntegrationTest() throws Exception {
    loadConfigFromResource();
  }

  private void loadConfigFromResource() throws Exception {
    if (resourceStream == null) {
      throw new IllegalArgumentException("Resource Not Found: " + CLIENT_CONFIG_RESOURCE_NAME);
    }
    _databricksConfig = new DatabricksClientConfiguration(resourceStream);

    _databricks = new DatabricksSession(_databricksConfig);
  }


  /**
   * Running this test lists all the clusters available in the corresponding databricks instance.
   * This test can be run every time so as to test databricks connection as it is only a read
   * operation on cluster. Add @test annotation to run this test.
   */

  public void testListClusters() {
    LOG.info("Begining list clusters test.");

    try {

      DatabricksSession databricks = _databricks;

      Iterator<InteractiveCluster> clusters = databricks.listClusters();
      while (clusters.hasNext()) {
        InteractiveCluster cluster = clusters.next();
        LOG.info("id: {}, name: {}, createdBy:{} , createdUserName: {}", cluster.Id, cluster.Name,
            cluster.CreatedBy, cluster.CreatorUserName);
      }
      LOG.info("Clusters List .toString: {}", clusters.toString());

    } catch (Exception e) {
      LOG.error("Error Exception Thrown while doing a list cluster : ", e);
    }
  }

  /**
   * Complete test to start run restart terminate a cluster. Caution !! Running this test will
   * create an actual cluster in configured databricks instance. Add @test annotation to run this
   * test.
   * 
   * @throws Exception
   */
  public void testSimpleClusterAutoscaling() throws Exception {
    long now = System.currentTimeMillis();
    String clusterName = "autoscaling-cluster_demo_" + now;
    Integer minWorkers = 0;
    Integer maxWorkers = 1;

    InteractiveCluster cluster = _databricks.createCluster(clusterName, minWorkers, maxWorkers)
        .withAutoTerminationMinutes(20).withSparkVersion("3.4.x-scala2.11")
        .withNodeType("i3.xlarge").create();

    Assert.assertEquals("Simple Autoscaling InteractiveCluster Name does NOT match expected NAME",
        clusterName, cluster.Name);

    String clusterId = cluster.Id;

    Assert.assertNotNull("Simple Autoscaling InteractiveCluster Id is NULL", clusterId);

    Assert.assertEquals(
        "Simple Autoscaling InteractiveCluster did NOT enter a PENDING state after create",
        ClusterState.PENDING, cluster.getState());

    while (cluster.getState() == ClusterState.PENDING) {
      // wait until cluster is properly started
      Thread.sleep(10000); // wait 10 seconds
    }

    Assert.assertEquals(
        "Simple Autoscaling InteractiveCluster did NOT enter a RUNNING state after create",
        ClusterState.RUNNING, cluster.getState());

    Assert.assertEquals(
        "Simple Autoscaling InteractiveCluster was NOT created with expected MINIMUM number of workers",
        cluster.AutoScale.MinWorkers, minWorkers);

    Assert.assertEquals(
        "Simple Autoscaling InteractiveCluster was NOT created with expected MAXIMUM number of workers",
        cluster.AutoScale.MaxWorkers, maxWorkers);

    cluster.restart();

    Assert.assertEquals(
        "Simple Autoscaling InteractiveCluster did NOT enter a RESTARTING state after restart",
        ClusterState.RESTARTING, cluster.getState());

    while (cluster.getState() == ClusterState.RESTARTING) {
      Thread.sleep(5000); // wait 5 seconds
    }

    Assert.assertEquals(
        "Simple Autoscaling InteractiveCluster did NOT enter a RUNNING state after restart",
        ClusterState.RUNNING, cluster.getState());

    minWorkers = 1;
    maxWorkers = 2;
    cluster = cluster.resize(minWorkers, maxWorkers);

    Assert.assertEquals(
        "Simple Autoscaling InteractiveCluster did NOT enter a RESIZING state after resize",
        ClusterState.RESIZING, cluster.getState());

    while (cluster.getState() == ClusterState.RESIZING) {
      Thread.sleep(5000); // wait 5 seconds
    }

    Assert.assertEquals(
        "Simple Autoscaling InteractiveCluster did NOT enter a RUNNING state after resize",
        ClusterState.RUNNING, cluster.getState());

    Assert.assertEquals(
        "Simple Autoscaling InteractiveCluster was NOT resized with expected MINIMUM number of workers",
        cluster.AutoScale.MinWorkers, minWorkers);

    Assert.assertEquals(
        "Simple Autoscaling InteractiveCluster was NOT resized with expected MAXIMUM number of workers",
        cluster.AutoScale.MaxWorkers, maxWorkers);

    cluster.terminate();

    Assert.assertEquals(
        "Simple Autoscaling InteractiveCluster did NOT enter a TERMINATING state after terminate",
        ClusterState.TERMINATING, cluster.getState());

    while (cluster.getState() == ClusterState.TERMINATING) {
      Thread.sleep(5000); // wait 5 seconds
    }

    Assert.assertEquals(
        "Simple Autoscaling InteractiveCluster did NOT enter a TERMINATED state after terminate",
        ClusterState.TERMINATED, cluster.getState());
  }


}
