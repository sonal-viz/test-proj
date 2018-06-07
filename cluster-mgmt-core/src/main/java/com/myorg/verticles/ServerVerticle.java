package com.myorg.verticles;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import com.myorg.auth.AuthUtils;
import com.myorg.error.AppErrorCode;
import com.myorg.error.ClusterManagementException;
import com.myorg.error.ErrorMessage;
import com.myorg.util.Constants;
import com.myorg.util.MonitoringUtil;
import com.myorg.util.RestResponseUtil;

import io.prometheus.client.Histogram;
import io.prometheus.client.vertx.MetricsHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

@Controller
public class ServerVerticle extends AbstractVerticle {

  private static final String AUTHORIZATION = "Authorization";
  private static final Logger LOG = LoggerFactory.getLogger(ServerVerticle.class);

  @Override
  public void start() throws Exception {
    super.start();

    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());
    router.options("/*").handler(this::optionsCall);
    router.route("/*").handler(this::authHandler);

    // Client endpoits
    router.get("/cm/v0.0.1/articles").handler(this::getAllArticlesHandler);
    router.post("/cm/v0.0.1/cluster").handler(this::createClusterHandler);
    router.get("/cm/v0.0.1/cluster").handler(this::getClusterHandler);
    router.post("/cm/v0.0.1/cluster/start").handler(this::startClusterHandler);
    router.post("/cm/v0.0.1/cluster/restart").handler(this::restartClusterHandler);
    router.post("/cm/v0.0.1/cluster/resize").handler(this::resizeClusterHandler);
    router.delete("/cm/v0.0.1/cluster/terminate").handler(this::terminateClusterHandler);

    // Monitoring endpoint
    router.get("/cm/v0.0.1/metrics").handler(new MetricsHandler());
    
    vertx.createHttpServer().requestHandler(router::accept)
        .listen(config().getInteger("http.port", 8090));
  }

  private void getAllArticlesHandler(RoutingContext routingContext) {
    MonitoringUtil.createClusterCounter.inc();
    vertx.eventBus().<String>send(Constants.VertxEventBusTopics.GET_ALL_ARTICLES, "", result -> {
      if (result.succeeded()) {
        routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
            .end(result.result().body());
      } else {
        routingContext.response().setStatusCode(500).end();
      }
    });
  }

  private void createClusterHandler(RoutingContext routingContext) {
    MonitoringUtil.createClusterCounter.inc();
    HttpServerResponse response = routingContext.response();
    JsonObject jsonObject = routingContext.getBodyAsJson();
    if (jsonObject == null) {
      ErrorMessage errorMessage =
          new ErrorMessage(HttpStatus.SC_BAD_REQUEST, AppErrorCode.REQUEST_VALIDATION_ERROR,
              "Request body Empty for create cluster Request", "");
      RestResponseUtil.sendErrorResponse(routingContext,
          new ClusterManagementException(errorMessage,
              new IllegalArgumentException("Request body Empty for create cluster Request")));
      return;
    }
    
    Histogram.Timer requestTimer = MonitoringUtil.createClusterLatency.startTimer();
    vertx.eventBus().<String>send(Constants.VertxEventBusTopics.CREATE_CLUSTER, jsonObject,
        reply -> {
          requestTimer.observeDuration();
          if (reply.succeeded()) {
            response.putHeader("content-type", "application/json").setStatusCode(HttpStatus.SC_OK)
                .end(reply.result().body());
          } else {
            LOG.error("Error occurred {} cause {} ", Constants.VertxEventBusTopics.CREATE_CLUSTER,
                reply.cause().getMessage());
            RestResponseUtil.sendErrorResponse(routingContext, reply.cause());
          }
        });
  }


  private void getClusterHandler(RoutingContext routingContext) {
    MonitoringUtil.getClusterCounter.inc();
    HttpServerResponse response = routingContext.response();
    String clusterId = routingContext.request().getParam("cluster_id");

    Histogram.Timer requestTimer = MonitoringUtil.getClusterLatency.startTimer();
    vertx.eventBus().<String>send(Constants.VertxEventBusTopics.GET_CLUSTER, clusterId, reply -> {
      if (reply.succeeded()) {
        requestTimer.observeDuration();
        response.putHeader("content-type", "application/json").setStatusCode(HttpStatus.SC_OK)
            .end(reply.result().body());
      } else {
        LOG.error("Error occurred {} cause {} ", Constants.VertxEventBusTopics.GET_CLUSTER,
            reply.cause().getMessage());
        RestResponseUtil.sendErrorResponse(routingContext, reply.cause());
      }
    });
  }

  private void startClusterHandler(RoutingContext routingContext) {
    MonitoringUtil.startClusterCounter.inc();
    HttpServerResponse response = routingContext.response();
    JsonObject jsonObject = routingContext.getBodyAsJson();

    Histogram.Timer requestTimer = MonitoringUtil.startClusterLatency.startTimer();
    vertx.eventBus().<String>send(Constants.VertxEventBusTopics.START_CLUSTER, jsonObject,
        reply -> {
          requestTimer.observeDuration();
          if (reply.succeeded()) {
            response.putHeader("content-type", "application/json").setStatusCode(HttpStatus.SC_OK)
                .end(reply.result().body());
          } else {
            LOG.error("Error occurred {} cause {} ", Constants.VertxEventBusTopics.START_CLUSTER,
                reply.cause().getMessage());
            RestResponseUtil.sendErrorResponse(routingContext, reply.cause());
          }
        });
  }

  private void restartClusterHandler(RoutingContext routingContext) {
    MonitoringUtil.restartClusterCounter.inc();
    HttpServerResponse response = routingContext.response();
    JsonObject jsonObject = routingContext.getBodyAsJson();

    Histogram.Timer requestTimer = MonitoringUtil.restartedClusterLatency.startTimer();
    vertx.eventBus().<String>send(Constants.VertxEventBusTopics.RESTART_CLUSTER, jsonObject,
        reply -> {
          requestTimer.observeDuration();
          if (reply.succeeded()) {
            response.putHeader("content-type", "application/json").setStatusCode(HttpStatus.SC_OK)
                .end(reply.result().body());
          } else {
            LOG.error("Error occurred {} cause {} ", Constants.VertxEventBusTopics.RESTART_CLUSTER,
                reply.cause().getMessage());
            RestResponseUtil.sendErrorResponse(routingContext, reply.cause());
          }
        });
  }

  private void resizeClusterHandler(RoutingContext routingContext) {
    MonitoringUtil.resizeClusterCounter.inc();
    HttpServerResponse response = routingContext.response();
    JsonObject jsonObject = routingContext.getBodyAsJson();

    Histogram.Timer requestTimer = MonitoringUtil.resizeClusterLatency.startTimer();
    vertx.eventBus().<String>send(Constants.VertxEventBusTopics.RESIZE_CLUSTER, jsonObject,
        reply -> {
          requestTimer.observeDuration();
          if (reply.succeeded()) {
            response.putHeader("content-type", "application/json").setStatusCode(HttpStatus.SC_OK)
                .end(reply.result().body());
          } else {
            LOG.error("Error occurred {} cause {} ", Constants.VertxEventBusTopics.RESIZE_CLUSTER,
                reply.cause().getMessage());
            RestResponseUtil.sendErrorResponse(routingContext, reply.cause());
          }
        });
  }

  private void terminateClusterHandler(RoutingContext routingContext) {
    MonitoringUtil.terminateClusterCounter.inc();
    HttpServerResponse response = routingContext.response();
    JsonObject jsonObject = routingContext.getBodyAsJson();

    Histogram.Timer requestTimer = MonitoringUtil.terminateClusterLatency.startTimer();
    vertx.eventBus().<String>send(Constants.VertxEventBusTopics.TERMINATE_CLUSTER, jsonObject,
        reply -> {
          requestTimer.observeDuration();
          if (reply.succeeded()) {
            response.putHeader("content-type", "application/json").setStatusCode(HttpStatus.SC_OK)
                .end(reply.result().body());
          } else {
            LOG.error("Error occurred {} cause {} ",
                Constants.VertxEventBusTopics.TERMINATE_CLUSTER, reply.cause().getMessage());
            RestResponseUtil.sendErrorResponse(routingContext, reply.cause());
          }
        });
  }

  /**
   * Handler to set allowed headers.
   *
   * @param routingContext
   */
  private void optionsCall(final RoutingContext routingContext) {
    try {
      routingContext.response().putHeader("Access-Control-Allow-Origin", "*")
          .putHeader("Access-Control-Allow-Methods", "GET, POST, PUT , DELETE, OPTIONS")
          .putHeader("Access-Control-Allow-Headers",
              "Content-Type,cache-control, x-requested-with,Authorization")
          .putHeader("Access-Control-Max-Age", "86400").end();
    } catch (final Exception e) {
      LOG.error("Exception while setting routing context option calls.", e);
    }
  }

  /**
   * Auth handler.
   *
   * @param routingContext
   */
  private void authHandler(final RoutingContext routingContext) {
    boolean isValidToken =
        AuthUtils.isTokenAuthenticated(routingContext.request().getHeader(AUTHORIZATION));
    if (!isValidToken) {
      routingContext.response().putHeader("Access-Control-Allow-Headers", "Content-Type")
          .putHeader("Access-Control-Allow-Headers", AUTHORIZATION)
          .putHeader("Access-Control-Allow-Origin", "*").setStatusCode(HttpStatus.SC_UNAUTHORIZED)
          .end("Unauthorized Request !!");
    } else {
      routingContext.next();
    }
  }

}
