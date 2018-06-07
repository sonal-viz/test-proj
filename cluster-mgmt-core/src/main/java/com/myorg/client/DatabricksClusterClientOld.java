package com.myorg.client;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myorg.databricks.CreateClusterRequest;
import com.myorg.databricks.CreateClusterResponse;
import com.myorg.dto.ClusterInfoDTO;
import com.myorg.entity.ClusterInfo;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration
@Deprecated
public class DatabricksClusterClientOld {

	private static final String URL = "https://myorg.cloud.databricks.com/api/";

	private static final Gson gson = new GsonBuilder().setLenient().create();

	private static final Logger LOG = LoggerFactory.getLogger(DatabricksClusterClientOld.class);

	private DatabricksClusterApiClient databricksClusterApiClient;

	// @Autowired
	// DatabricksMapper databricksMapper;

	@PostConstruct
	public void initialize() {
		databricksClusterApiClient = getBuilder().create(DatabricksClusterApiClient.class);
	}

	public Retrofit getBuilder() {
		return new Retrofit.Builder().baseUrl(URL).client(getOkHttpClient())
				.addConverterFactory(GsonConverterFactory.create(gson)).build();
	}

	private OkHttpClient getOkHttpClient() {
		OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
		okHttpClient.addInterceptor(new Interceptor() {

			@Override
			public Response intercept(Chain chain) throws IOException {
				Request request = chain.request().newBuilder().header("Authorization", "Basic sdfasdf").build();
				// "Basic XCVZXC"
				// Basic VXZV
				return chain.proceed(request);
			}
		}).readTimeout(120, TimeUnit.SECONDS).connectTimeout(120, TimeUnit.SECONDS);

		return okHttpClient.build();
	}

	public String createCluster(CreateClusterRequest createClusterRequest) throws IOException {

		Call<CreateClusterResponse> call = databricksClusterApiClient.createCluster(createClusterRequest);
		retrofit2.Response<CreateClusterResponse> res = null;
		res = call.execute();
		return res.body().getCluster_id();
	}

	public ClusterInfo createCluster(ClusterInfoDTO clusterInfoDTO) throws IOException {
		// CreateClusterRequest createClusterRequest = new
		// CreateClusterRequest();
		// createClusterRequest.setCluster_name(clusterInfoDTO.getClusterName());
		// createClusterRequest.setSpark_version(clusterInfoDTO.getSparkVersion());
		// createClusterRequest.setNode_type_id(clusterInfoDTO.getNodeTypeId());
		//
		// AutoscaleParameters autoscaleParameters = new AutoscaleParameters();
		// autoscaleParameters.setMax_workers(clusterInfoDTO.getAutoscale().getMaxWorkers());
		// autoscaleParameters.setMin_workers(clusterInfoDTO.getAutoscale().getMinWorkers());
		// createClusterRequest.setAutoscale(autoscaleParameters);
		// createCluster(databricksMapper.mapClusterInfoToCreateClusterRequest(clusterInfoDTO));
		return null;

	}
}
