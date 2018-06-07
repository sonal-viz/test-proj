package com.myorg.auth;

import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.myorg.util.JsonUtil;

public class SessionUtils {

  private static Logger log = LoggerFactory.getLogger(SessionUtils.class);
  private static String SEPARATOR = ".";
  private static String INVALID_TOKEN = "Invalid Token";

  @SuppressWarnings("static-access")
  public static SessionUser getUser(String token) throws InvalidAuthenticationToken {

    log.info("User session with token {}", token);
    String[] tokens = StringUtils.split(token, SEPARATOR);

    if (tokens.length < 3) {
      log.error("Invalid token length");
      throw new InvalidAuthenticationToken(INVALID_TOKEN);
    }

    return setUserInfo(token);
  }

  @SuppressWarnings("static-access")
  public static SessionUser setUserInfo(String token) {

    SessionUser sessionUser = new SessionUser();
    String[] tokens = StringUtils.split(token, SEPARATOR);

    Base64 decoder = new Base64(true);
    String decodedToken = new String(decoder.decodeBase64(tokens[1]));

    try {
      AuthToken authToken = JsonUtil.createObjectFromString(decodedToken, AuthToken.class);
      sessionUser.setUserName(authToken.getUserName());
      sessionUser.setRoles(authToken.getAuthorities());
      sessionUser.setPermissions(authToken.getScopes());
    } catch (JsonGenerationException e) {
      log.error("[OAuth] Exception while generating a Json from decoded token {}", decodedToken);
    } catch (JsonMappingException e) {
      log.error("[OAuth] Exception while mapping the Json token {}", decodedToken);
    } catch (IOException e) {
      log.error("[OAuth] Exception while reading a valid Json token {}", decodedToken);
    }

    return sessionUser;
  }

}
