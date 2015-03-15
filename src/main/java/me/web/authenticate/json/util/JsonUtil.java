package me.web.authenticate.json.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.web.authenticate.core.Authenticate;

import java.io.IOException;

/**
 * Created by craigbrookes on 15/03/15.
 */
public class JsonUtil {

  public static String toJSON(Authenticate auth)throws JsonProcessingException{
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(auth);
  }

  public static Authenticate fromJSON(String auth)throws IOException{
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(auth,Authenticate.class);
  }

}
