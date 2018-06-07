package com.myorg.verticles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.myorg.dto.ClusterInfoDTO;
import com.myorg.dto.ResizeClusterDTO;
import com.myorg.service.IClusterService;
import com.myorg.util.Constants;
import com.myorg.util.JsonUtil;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

@Component
public class ClusterMgmtVerticle extends AbstractVerticle {

  @Autowired
  private IClusterService clusterService;

  private static final Logger LOG = LoggerFactory.getLogger(ClusterMgmtVerticle.class);

  Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .create();

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    super.start();
    vertx.eventBus().localConsumer(Constants.VertxEventBusTopics.CREATE_CLUSTER,
        message -> vertx.<String>executeBlocking(future -> {
          try {
            ClusterInfoDTO clusterDTO =
                gson.fromJson(message.body().toString(), ClusterInfoDTO.class);
            String clusterId = null;
            ClusterInfoDTO opClusterDTO = clusterService.createCluster(clusterDTO);
            
            //need to pass the entire json
            future.complete(JsonUtil.createJsonFromObject(opClusterDTO));
          } catch (Exception e) {
            LOG.error("Create Cluster Failed. Reason: ", e);
            future.fail(e);
          }
        }, res -> {
          if (res.succeeded()) {
            message.reply(res.result());
          } else {
            message.fail(-1, res.cause().getMessage());
          }
        }));

    vertx.eventBus().localConsumer(Constants.VertxEventBusTopics.GET_CLUSTER,
        message -> vertx.<String>executeBlocking(future -> {
          ClusterInfoDTO clusterInfoDTO = null;
          try {
            clusterInfoDTO = clusterService.getCluster(message.body().toString());
            future.complete(gson.toJson(clusterInfoDTO));
          } catch (Exception e) {
            LOG.error("Get Cluster Failed. Reason: ", e);
            future.fail(e);
          }
        }, res -> {
          if (res.succeeded()) {
            message.reply(res.result());
          } else {
            message.fail(-1, res.cause().toString());
          }
        }));

    vertx.eventBus().localConsumer(Constants.VertxEventBusTopics.START_CLUSTER,
        message -> vertx.<String>executeBlocking(future -> {
          String startedClusterId = null;
          try {
            JsonObject clusterIdJson = gson.fromJson(message.body().toString(), JsonObject.class);
            String clusterId = clusterIdJson.get("cluster_id").getAsString();
            startedClusterId = clusterService.startCluster(clusterId);
            future.complete(gson.toJson(startedClusterId));
          } catch (Exception e) {
            LOG.error("Start Cluster Failed. Reason: ", e);
            future.fail(e);
          }
        }, res -> {
          if (res.succeeded()) {
            message.reply(res.result());
          } else {
            message.fail(-1, res.cause().toString());
          }
        }));

    vertx.eventBus().localConsumer(Constants.VertxEventBusTopics.RESTART_CLUSTER,
        message -> vertx.<String>executeBlocking(future -> {
          String startedClusterId = null;
          try {
            JsonObject clusterIdJson = gson.fromJson(message.body().toString(), JsonObject.class);
            String clusterId = clusterIdJson.get("cluster_id").getAsString();
            startedClusterId = clusterService.restartCluster(clusterId);
            future.complete(gson.toJson(startedClusterId));
          } catch (Exception e) {
            LOG.error("Restart Cluster Failed. Reason: ", e);
            future.fail(e);
          }
        }, res -> {
          if (res.succeeded()) {
            message.reply(res.result());
          } else {
            message.fail(-1, res.cause().toString());
          }
        }));

    vertx.eventBus().localConsumer(Constants.VertxEventBusTopics.RESIZE_CLUSTER,
        message -> vertx.<String>executeBlocking(future -> {
          String resizedClusterId = null;
          try {
            ResizeClusterDTO resizeClusterDTO =
                gson.fromJson(message.body().toString(), ResizeClusterDTO.class);
            LOG.debug("Resizing cluster with following configuration: {} ", gson.toJson(resizeClusterDTO));
            resizedClusterId = clusterService.resizeCluster(resizeClusterDTO);
            future.complete(gson.toJson(resizedClusterId));
          } catch (Exception e) {
            LOG.error("Resizing Cluster Failed. Reason: ", e);
            future.fail(e);
          }
        }, res -> {
          if (res.succeeded()) {
            message.reply(res.result());
          } else {
            message.fail(-1, res.cause().toString());
          }
        }));

    vertx.eventBus().localConsumer(Constants.VertxEventBusTopics.TERMINATE_CLUSTER,
        message -> vertx.<String>executeBlocking(future -> {
          String deletedClusterId = null;
          try {
            JsonObject clusterIdJson = gson.fromJson(message.body().toString(), JsonObject.class);
            String clusterId = clusterIdJson.get("cluster_id").getAsString();
            deletedClusterId = clusterService.terminateCluster(clusterId);
            future.complete(gson.toJson(deletedClusterId));
          } catch (Exception e) {
            LOG.error("Terminate Cluster Failed. Reason: ", e);
            future.fail(e);
          }
        }, res -> {
          if (res.succeeded()) {
            message.reply(res.result());
          } else {
            message.fail(-1, res.cause().toString());
          }
        }));
  }
}
