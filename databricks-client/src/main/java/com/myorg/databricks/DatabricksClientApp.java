package com.myorg.databricks;

import com.myorg.databricks.client.DatabricksSession;
import com.myorg.databricks.client.JobsClient;
import com.myorg.databricks.client.entities.jobs.JobDTO;
import com.myorg.databricks.cluster.SparkVersion;
import com.myorg.databricks.config.DatabricksClientConfiguration;
import com.myorg.databricks.job.AutomatedNotebookJob;
import com.myorg.databricks.job.AutomatedNotebookJobRun;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration2.Configuration;

public class DatabricksClientApp {

	public static final String CLIENT_CONFIG_RESOURCE_NAME = "databricks-client.properties";

	public static void main(String[] args) {
		System.out.println("DatabricksClient- Begin Reading Resource");

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream resourceStream = loader.getResourceAsStream(CLIENT_CONFIG_RESOURCE_NAME);
		if (resourceStream == null) {
			throw new IllegalArgumentException("Resource Not Found: " + CLIENT_CONFIG_RESOURCE_NAME);
		}

		System.out.println("Resource Loaded...");
		try {
			DatabricksClientConfiguration databricksConfig = new DatabricksClientConfiguration(resourceStream);
			DatabricksSession databricks = new DatabricksSession(databricksConfig);

			System.out.println("TOKEN: " + databricksConfig.getClientPassword());

			List<SparkVersion> versions = databricks.getSparkVersions();

			for (SparkVersion ver : versions) {
				System.out.println(ver.Key);
			}

		} catch (Exception e) {
			System.out.println("Error Exception Thrown: " + e);
			e.printStackTrace();
		}
	}
}
