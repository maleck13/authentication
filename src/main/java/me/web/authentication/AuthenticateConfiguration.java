package me.web.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


public class AuthenticateConfiguration extends Configuration{

  @NotEmpty
  @JsonProperty("salt")
  private String salt;

  @Valid
  @NotNull
  @JsonProperty("database")
  private DataSourceFactory database = new DataSourceFactory();


  public DataSourceFactory getDataSourceFactory() {
    return database;
  }

  @Valid
  @NotNull
  @JsonProperty
  private  RedisConfiguration redis = new RedisConfiguration();

  public RedisConfiguration getRedis(){
    return redis;
  }

  public String getSalt(){
    return  salt;
  }
}
