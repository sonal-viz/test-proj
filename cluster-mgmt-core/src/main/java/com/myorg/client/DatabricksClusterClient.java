package com.myorg.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.myorg.databricks.client.ClustersClient;
import com.myorg.databricks.client.DatabricksSession;
import com.myorg.databricks.client.HttpException;
import com.myorg.databricks.cluster.ClusterConfigException;
import com.myorg.databricks.cluster.InteractiveCluster;
import com.myorg.databricks.config.DatabricksClientConfiguration;
import com.myorg.dto.AutoScaleDTO;
import com.myorg.dto.AwsAttributesDTO;
import com.myorg.dto.ClusterInfoDTO;
import com.myorg.dto.ResizeClusterDTO;
import com.myorg.entity.ClusterInfo;
import com.myorg.error.AppErrorCode;
import com.myorg.error.ClusterManagementException;
import com.myorg.error.ErrorMessage;
import com.myorg.mapper.ClusterInfoMapper;
import com.myorg.util.JsonUtil;

@Component
public class DatabricksClusterClient implements ClusterClient {

	// @Autowired
	// DatabricksMapper databricksMapper;

	private static final Logger LOG = LoggerFactory.getLogger(DatabricksClusterClient.class);

	public static final String CLIENT_CONFIG_RESOURCE_NAME = "application.properties";

	ClassLoader loader = Thread.currentThread().getContextClassLoader();
	InputStream resourceStream = loader.getResourceAsStream(CLIENT_CONFIG_RESOURCE_NAME);
	DatabricksSession databricksSession;
	DatabricksClientConfiguration databricksConfig;

	public DatabricksClusterClient() throws IOException, ConfigurationException {
		loadConfigFromResource();
	}

	private void loadConfigFromResource() throws IOException, ConfigurationException {
		if (resourceStream == null) {
			throw new IllegalArgumentException("Resource Not Found: " + CLIENT_CONFIG_RESOURCE_NAME);
		}
		databricksConfig = new DatabricksClientConfiguration(resourceStream);

		databricksSession = new DatabricksSession(databricksConfig);
	}

	@Override
	public ClusterInfo createCluster(ClusterInfoDTO clusterInfoDTO) throws IOException {
		ClusterInfo clusterInfo = null;
		try {
			LOG.info("Attempting to create cluster with following config: {}", clusterInfoDTO.toString());
			InteractiveCluster cluster = databricksSession
					.createCluster(clusterInfoDTO.clusterName, clusterInfoDTO.autoscale.minWorkers,
							clusterInfoDTO.autoscale.maxWorkers)
					.withAutoTerminationMinutes(clusterInfoDTO.autoTerminationMinutes)
					.withSparkVersion(clusterInfoDTO.sparkVersion).withNodeType(clusterInfoDTO.nodeTypeId).create();
			ClustersClient clustersClient = databricksSession.getClustersClient();
			com.myorg.databricks.client.entities.clusters.ClusterInfoDTO clusterInfoClientDTO = clustersClient
					.getCluster(cluster.Id);
			clusterInfo = mapClusterObjToClusterInfo(cluster, clusterInfoClientDTO);

		} catch (ClusterConfigException e) {
			ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					AppErrorCode.CLUSTER_SERVICE_ERROR,
					"Could not create databricks client using configs" + ", " + e.getMessage(),
					Arrays.toString(e.getStackTrace()));
			throw new ClusterManagementException(errorMessage, e);
		} catch (HttpException e) {
			ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					AppErrorCode.CLUSTER_SERVICE_ERROR,
					"Cluster create failed, Could not connect to databricks server using configs" + ", "
							+ e.getMessage(),
					Arrays.toString(e.getStackTrace()));
			throw new ClusterManagementException(errorMessage, e);
		}
		return clusterInfo;
	}

	@Override
	public ClusterInfo getCluster(ClusterInfo dbCluster) throws IOException {
		String clusterId = dbCluster.getClusterId();
		ClusterInfo clusterInfo = null;
		try {
			InteractiveCluster cluster = databricksSession.getCluster(clusterId);
			ClustersClient clustersClient = databricksSession.getClustersClient();
			com.myorg.databricks.client.entities.clusters.ClusterInfoDTO clusterInfoDTO = clustersClient
					.getCluster(clusterId);
			clusterInfo = mapClusterObjToClusterInfo(cluster, clusterInfoDTO);
		} catch (ClusterConfigException e) {
			ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					AppErrorCode.CLUSTER_SERVICE_ERROR,
					"Could not get databricks cluster using provided configs" + ", " + e.getMessage(),
					Arrays.toString(e.getStackTrace()));
			throw new ClusterManagementException(errorMessage, e);
		} catch (HttpException e) {
			ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					AppErrorCode.CLUSTER_SERVICE_ERROR,
					"Get cluster failed for cluster_id: " + clusterId + ", " + e.getMessage(),
					Arrays.toString(e.getStackTrace()));
			throw new ClusterManagementException(errorMessage, e);
		}
		return clusterInfo;
	}

	@Override
	public String startCluster(String clusterId) throws IOException {
		try {
			databricksSession.getClustersClient().start(clusterId);
		} catch (HttpException e) {
			ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					AppErrorCode.CLUSTER_SERVICE_ERROR,
					"Start cluster failed for cluster_id: " + clusterId + ", " + e.getMessage(),
					Arrays.toString(e.getStackTrace()));
			throw new ClusterManagementException(errorMessage, e);
		}
		return clusterId;
	}

	@Override
	public String restartCluster(String clusterId) throws IOException {
		try {
			databricksSession.getClustersClient().reStart(clusterId);
		} catch (HttpException e) {
			ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					AppErrorCode.CLUSTER_SERVICE_ERROR,
					"Restart cluster failed for cluster_id: " + clusterId + ", " + e.getMessage(),
					Arrays.toString(e.getStackTrace()));
			throw new ClusterManagementException(errorMessage, e);
		}
		return clusterId;
	}

	@Override
	public String resizeCluster(ResizeClusterDTO resizeClusterDTO) throws IOException {
		String clusterId = resizeClusterDTO.clusterId;
		try {
			if (resizeClusterDTO.numWorkers != null) {
				databricksSession.getClustersClient().resize(clusterId, resizeClusterDTO.numWorkers);
			} else if (resizeClusterDTO.autoscale != null) {
				databricksSession.getClustersClient().resize(clusterId, resizeClusterDTO.autoscale.minWorkers,
						resizeClusterDTO.autoscale.maxWorkers);
			}
		} catch (HttpException e) {
			ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					AppErrorCode.DATABRICKS_SERVER_ERROR,
					"Resize cluster failed for cluster_id: " + clusterId + ", " + e.getMessage(),
					Arrays.toString(e.getStackTrace()));
			throw new ClusterManagementException(errorMessage, e);
		}
		return clusterId;
	}

	@Override
	public String terminateCluster(ClusterInfo cluster) throws IOException {
		String clusterId = cluster.getClusterId();
		try {
			databricksSession.getClustersClient().delete(clusterId);
		} catch (HttpException e) {
			ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					AppErrorCode.CLUSTER_SERVICE_ERROR,
					"Terminate cluster failed for cluster_id: " + clusterId + ", " + e.getMessage(),
					Arrays.toString(e.getStackTrace()));
			throw new ClusterManagementException(errorMessage, e);
		}
		return clusterId;
	}

	public ClusterInfo mapClusterObjToClusterInfo(InteractiveCluster cluster,
			com.myorg.databricks.client.entities.clusters.ClusterInfoDTO clusterInfoDTO) {

		ClusterInfo clusterInfo = new ClusterInfo();
		clusterInfo.setAutoTerminationMinutes(cluster.AutoTerminationMinutes);
		try {
			clusterInfo.setAutoScale(JsonUtil.createObjectFromString(JsonUtil.createJsonFromObject(cluster.AutoScale),
					AutoScaleDTO.class));
		} catch (IOException e) {
			LOG.error("Could not marshal parameters for setting in cluster info object. Object :{} ",
					cluster.AutoScale.toString());
		}
		AwsAttributesDTO awsAttributesDTO = ClusterInfoMapper.MAPPER.getAwsAttributesDTO(cluster.AwsAttributes);
		clusterInfo.setAwsAttributes(awsAttributesDTO);
		clusterInfo.setClusterCores(clusterInfoDTO.ClusterCores);
		clusterInfo.setClusterId(getStringAttribute(cluster.Id));
		clusterInfo.setClusterLogConf(getStringAttribute(cluster.ClusterLogConf));
		clusterInfo.setClusterSource(getStringAttribute(cluster.ClusterSource));
		clusterInfo.setClusterCreatedBy(getStringAttribute(cluster.CreatedBy));
		clusterInfo.setClusterMemoryMb(clusterInfoDTO.ClusterMemoryMb);
		clusterInfo.setClusterName(getStringAttribute(cluster.Name));
		// clusterInfo.setCustomTags(cluster.CustomTags);
		// clusterInfo.setDefaultTags(cluster.DefaultTags);
		clusterInfo.setEnableElasticDisk(cluster.ElasticDiskEnabled);
		clusterInfo.setJdbcPort(cluster.JdbcPort);
		clusterInfo.setLastActivityTime(clusterInfoDTO.LastActivityTime);
		clusterInfo.setNodeTypeId(getStringAttribute(cluster.Driver));
		clusterInfo.setNumWorkers(cluster.NumWorkers);
		clusterInfo.setSparkContextId(cluster.SparkContextId);
		clusterInfo.setSparkVersionKey(getStringAttribute(cluster.SparkVersion.Key));
		clusterInfo.setStartTime(cluster.StartTime);
		clusterInfo.setState(clusterInfoDTO.State);
		clusterInfo.setStateMessage(clusterInfoDTO.StateMessage);
		clusterInfo.setTerminatedTime(clusterInfoDTO.TerminatedTime);

		// map some more remaining params here from client obj to dto .
		// dto in both the code are having slightly different structure have to
		// map them.
		return clusterInfo;
	}

	public String getStringAttribute(Object o) {
		if (o != null) {
			if (o instanceof String)
				return (String) o;
			return o.toString();
		} else
			return "";
	}

}
