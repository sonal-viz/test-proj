package com.myorg.util;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

public class JsonUtil {

  private static ObjectMapper mapper;

  static {
    mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.enableDefaultTypingAsProperty(DefaultTyping.JAVA_LANG_OBJECT, "class");
    mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
    mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
  }

  public static synchronized String createJsonFromObject(Object obj)
      throws JsonGenerationException, JsonMappingException, IOException {
    return mapper.writeValueAsString(obj);
  }

  public static synchronized <T> T createObjectFromString(String jsonString, Class<T> c)
      throws JsonParseException, JsonMappingException, IOException {
    return (T) mapper.readValue(jsonString, c);
  }

  // @SuppressWarnings("unchecked")
  // public static synchronized <T> T createMapFromString(String jsonString)
  // throws JsonParseException, JsonMappingException, IOException {
  // return (T) mapper.readValues(jsonString, new TypeReference<HashMap<String, String>>() {});
  // }
  //
  // public static synchronized JsonNode createMapFromFile(final String filePath) throws IOException
  // {
  // return mapper.readTree(new FileInputStream(filePath));
  // }

}
