package me.web.authenticate.core;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Authenticate {

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

  public String getPassword() {
    return password;
  }

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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Authenticate that = (Authenticate) o;

    if (id != that.id) return false;
    if (!userid.equals(that.userid)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + userid.hashCode();
    return result;
  }
}
