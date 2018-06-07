package com.myorg.client;

import com.myorg.databricks.CreateClusterRequest;
import com.myorg.databricks.CreateClusterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DatabricksClusterApiClient {

  @POST("2.0/clusters/create")
  Call<CreateClusterResponse> createCluster(@Body CreateClusterRequest createClusterRequest);
}
