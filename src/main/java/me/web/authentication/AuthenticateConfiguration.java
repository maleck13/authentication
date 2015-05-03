package me.web.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


public class AuthenticateConfiguration extends Configuration{

  @NotEmpty
  @JsonProperty("pepper")
  private String pepper;

  @NotNull
  @JsonProperty("loginValid")
  private int loginValid;

  @Valid
  @NotNull
  @JsonProperty("database")
  private DataSourceFactory database = new DataSourceFactory();


  public DataSourceFactory getDataSourceFactory() {
    return database;
  }


  public String getPepper(){
    return  pepper;
  }

  public int getLoginValid(){
    return loginValid;
  }
}
