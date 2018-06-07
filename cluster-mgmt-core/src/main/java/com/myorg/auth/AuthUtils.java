package com.myorg.auth;

import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.myorg.util.JsonUtil;

public class AuthUtils {

  private static Logger log = LoggerFactory.getLogger(AuthUtils.class);
  private static String SEPARATOR = ".";

  @SuppressWarnings("static-access")
  public static boolean isTokenAuthenticated(String token) {
    AuthToken authToken = null;

    if (StringUtils.isBlank(token)) {
      log.warn("OAuth token is blank");
      return false;
    }

    String[] tokens = StringUtils.split(token, SEPARATOR);
    if (tokens.length < 3) {
      log.warn("OAuth token length is less than 3", token);
      return false;
    }

    Base64 decoder = new Base64(true);
    String decodedToken = new String(decoder.decodeBase64(tokens[1]));
    try {
      authToken = JsonUtil.createObjectFromString(decodedToken, AuthToken.class);
    } catch (JsonGenerationException e) {
      log.error("[OAuth] Exception while generating a json from decoded token\n", decodedToken);
      return false;
    } catch (JsonMappingException e) {
      log.error("[OAuth] Exception while mapping the Json token\n", decodedToken);
      return false;
    } catch (IOException e) {
      log.error("[OAuth] Exception while reading a valid Json token\n", decodedToken);
      return false;
    }

    return isValidAuthority(authToken);
  }

  private static boolean isValidAuthority(AuthToken authToken) {
    return true;
  }

}
