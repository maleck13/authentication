package me.web.authenticate;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Created by craigbrookes on 15/03/15.
 */
public class RedisConfiguration {

  @JsonProperty
  private String hostname;

  @Min(1)
  @Max(65535)
  @JsonProperty
  private Integer port;

  @JsonProperty
  private String password;

  public String getHostname()
  {
    return hostname;
  }

  public void setHostname(String hostname)
  {
    this.hostname = hostname;
  }

  public Integer getPort()
  {
    return port;
  }

  public void setPort(Integer port)
  {
    this.port = port;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
