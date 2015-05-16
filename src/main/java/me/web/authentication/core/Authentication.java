package me.web.authentication.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "authentication")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Authentication {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotEmpty
  @Column(name = "userid", nullable = false,unique = true)
  private String userid;

  @NotEmpty
  @Column(name = "password", nullable = false)
  private String password;


  @Column(name = "authtoken", nullable = true,unique = true)
  private String authtoken;

  @Column(name = "valid",nullable = true)
  private Date valid;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getUserid() {
    return userid;
  }

  public void setUserid(String username) {
    this.userid = username;
  }

  @JsonIgnore
  public String getPassword() {
    return password;
  }

  @JsonProperty
  public void setPassword(String password) {
    this.password = password;
  }

  public String getAuthtoken() {
    return authtoken;
  }

  public void setAuthtoken(String authtoken) {
    this.authtoken = authtoken;
  }

  public Date getValid() {
    return valid;
  }

  public void setValid(Date valid) {
    this.valid = valid;
  }

  @Override
  public String toString() {
    return "Authentication{" +
        "userid='" + userid + '\'' +
        ", password='" + password + '\'' +
        ", authtoken='" + authtoken + '\'' +
        '}';
  }
}
