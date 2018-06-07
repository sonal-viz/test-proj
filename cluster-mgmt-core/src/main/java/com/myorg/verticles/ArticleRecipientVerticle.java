package com.myorg.verticles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myorg.service.ArticleService;
import com.myorg.util.Constants;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;

@Component
public class ArticleRecipientVerticle extends AbstractVerticle {


  private static final ObjectMapper mapper = Json.mapper;
  private static final Logger LOG = LoggerFactory.getLogger(ArticleRecipientVerticle.class);

  @Autowired
  private ArticleService articleService;

  @Override
  public void start() throws Exception {
    super.start();
    vertx.eventBus().<String>consumer(Constants.VertxEventBusTopics.GET_ALL_ARTICLES)
        .handler(getAllArticleService(articleService));
  }

  private Handler<Message<String>> getAllArticleService(ArticleService service) {
    return msg -> vertx.<String>executeBlocking(future -> {
      try {
        future.complete(mapper.writeValueAsString(service.getAllArticle()));
      } catch (JsonProcessingException e) {
        LOG.error("Failed to serialize result");
        future.fail(e);
      }
    }, result -> {
      if (result.succeeded()) {
        msg.reply(result.result());
      } else {
        msg.reply(result.cause().toString());
      }
    });
  }
}
