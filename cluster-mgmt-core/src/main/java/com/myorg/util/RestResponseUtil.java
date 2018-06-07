package com.myorg.util;

import java.io.IOException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.myorg.error.AppErrorCode;
import com.myorg.error.ErrorMessage;

import io.vertx.ext.web.RoutingContext;

public class RestResponseUtil {

  private static final Logger LOG = LoggerFactory.getLogger(RestResponseUtil.class);

  public static void sendErrorResponse(RoutingContext routingCtx, Throwable th) {
    ErrorMessage errorMessage;
    try {

      try {
        String message = th.getMessage().substring(th.getMessage().indexOf("{"));
        errorMessage = JsonUtil.createObjectFromString(message, ErrorMessage.class);
      } catch (IOException e) {
        LOG.error(
            "IO Exception while getting ErrorMessage Object by deserializing throwable message : {}",
            th.getMessage(), e);
        errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            AppErrorCode.CLUSTER_SERVICE_ERROR,
            AppErrorCode.getErrorMessage(AppErrorCode.CLUSTER_SERVICE_ERROR),
            th.getMessage() + "\n" + Arrays.toString(th.getStackTrace()));
      }

      try {
        routingCtx.response().putHeader("content-type", "application/json")
            .putHeader("Access-Control-Allow-Origin", "*")
            .putHeader("Access-Control-Allow-Headers", "Content-Type")
            .putHeader("Access-Control-Allow-Headers", "Authorization")
            .putHeader("Access-Control-Allow-Methods", "GET, POST, PUT , OPTIONS")
            .setStatusCode(errorMessage.getHttpStatus())
            .end(JsonUtil.createJsonFromObject(errorMessage));
      } catch (final IOException e) {
        LOG.error(
            "IO Exception while serializing the ErrorMessage object for response, Sending blank response. ErrorMessage object :{} ",
            errorMessage, e);
        routingCtx.response().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).end();
      }
    } catch (final Exception e) {
      LOG.error("Exception occured, Sending blank response. ErrorMessage object :{} ", e);
      routingCtx.response().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).end();
    }
  }

  // TODO : write success response handler too if it gets repetitive with too many params.

  private RestResponseUtil() {
    // Just to defeat instantiation.
  }

}
