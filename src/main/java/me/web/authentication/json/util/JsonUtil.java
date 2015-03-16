package me.web.authentication.json.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.web.authentication.core.Authentication;

import java.io.IOException;


public class JsonUtil {

  public static String toJSON(Authentication auth)throws JsonProcessingException{
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(auth);
  }

  public static Authentication fromJSON(String auth)throws IOException{
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(auth,Authentication.class);
  }

}
