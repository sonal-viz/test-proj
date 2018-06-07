package com.myorg.app;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.myorg.util.Constants;
import com.myorg.verticles.ArticleRecipientVerticle;
import com.myorg.verticles.ClusterMgmtVerticle;
import com.myorg.verticles.ServerVerticle;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.hotspot.DefaultExports;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.dropwizard.Match;
import io.vertx.ext.dropwizard.MatchType;

@SpringBootApplication
@Configuration
@EnableJpaRepositories("com.myorg.repository")
@EntityScan("com.myorg.entity")
@ComponentScan(basePackages = {"com.myorg"})
public class ClusterMgmtApp {

  private static final Logger LOG = LoggerFactory.getLogger(ClusterMgmtApp.class);

  @Autowired
  private ServerVerticle serverVerticle;

  @Autowired
  private ArticleRecipientVerticle serviceVerticle;

  @Autowired
  private ClusterMgmtVerticle clusterMgmtVerticle;


  static DropwizardMetricsOptions metricsOptions =
      new DropwizardMetricsOptions().setEnabled(true).setRegistryName(Constants.METRICS_REGISTRY_CM)
          .addMonitoredEventBusHandler(new Match().setValue(Constants.METRICS_EVENT_BUS_ADDRESS))
          .addMonitoredHttpServerUri(new Match().setValue("/.*").setType(MatchType.REGEX));

  public static final Vertx vertx = Vertx.vertx(
      new VertxOptions().setBlockedThreadCheckInterval(1000L * 60 * 60).setWorkerPoolSize(50));

  public static void main(String[] args) {
    SpringApplication.run(ClusterMgmtApp.class, args);
  }

  @PostConstruct
  public void deployVerticle() {
    vertx.deployVerticle(serverVerticle);
    vertx.deployVerticle(serviceVerticle);
    vertx.deployVerticle(clusterMgmtVerticle);
    LOG.info("deployed verticles and application started.");


    // Get the Dropwizard metrics registry and attach it to the Prometheus registry.
    MetricRegistry metricRegistry =
        SharedMetricRegistries.getOrCreate(Constants.METRICS_REGISTRY_CM);
    CollectorRegistry.defaultRegistry.register(new DropwizardExports(metricRegistry));

    // Add metrics about CPU, JVM memory etc.
    DefaultExports.initialize();

  }

}
