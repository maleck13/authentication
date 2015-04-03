package me.web.authentication.service;


import io.dropwizard.hibernate.UnitOfWork;
import me.web.authentication.core.Authentication;
import me.web.authentication.dao.AuthenticateDao;
import me.web.authentication.exceptions.UnauthorisedException;
import me.web.authentication.json.util.JsonUtil;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import sun.jvm.hotspot.tools.jcore.ByteCodeRewriter;

import javax.servlet.http.Cookie;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AuthenticateService {

  private AuthenticateDao authenticateDao;
  private final JedisPool jedisPool;

  Logger authLog = LoggerFactory.getLogger(AuthenticateService.class);

  public AuthenticateService(final AuthenticateDao authenticateDao, JedisPool jedis) {
    this.authenticateDao = authenticateDao;
    this.jedisPool = jedis;
  }

  public Authentication findUser(String id){
    authLog.debug("finding user " + id);
    Authentication auth = authenticateDao.findByUserId(id);
    if(null == auth){
      authLog.debug("failed to find by user id checking by authtoken");
      auth = authenticateDao.findUserByAuthToken(id);
    }
    return auth;
  }

  public String hashPassword(String pass)throws Exception{
    if(null == pass) throw new InvalidParameterException("invalid password");
    String hash = BCrypt.hashpw(pass, BCrypt.gensalt(12));
    return hash;

  }

  private String getPass(String userPass){
    String pepper = System.getProperty("pepper");
    return userPass + pepper;
  }

  public Authentication setUp(Authentication authentication){
    try {
      String hashed =  hashPassword(getPass(authentication.getPassword()));
      authentication.setPassword(hashed);
      return authenticateDao.createUpdate(authentication);
    }catch(ConstraintViolationException e){
      throw new WebApplicationException(409);
    }catch (Exception  ex){
      throw new WebApplicationException(500);
    }
  }

  public void delete(Authentication authentication){

  }

  public static Cookie getAuthCookie(Cookie[] cookies){
    if(null == cookies || cookies.length == 0) return null;

    for(Cookie c : cookies){
      if(c.getName().equals("auth")){
        return c;
      }
    }
    return null;
  }

  public Authentication validate(Authentication authentication){
    Authentication auth = null;
    Boolean valid;
    Date now = new Date();

    if(null != authentication.getUserid()) {
      auth = findUser(authentication.getUserid());
    }

    if(null == auth){
      throw new UnauthorisedException();
    }

    valid = (auth.getUserid().equals(authentication.getUserid()));

    if(valid && null != authentication.getPassword()){
        valid = (BCrypt.checkpw(getPass(authentication.getPassword()),auth.getPassword()));
      //no password sent check temp auth token
    }else if(valid && null != authentication.getAuthtoken()){
      valid = (authentication.getAuthtoken().equals(auth.getAuthtoken()));
      valid = (valid && null != auth.getValid() && auth.getValid().getTime() < now.getTime());
    }

    if(!valid){
      throw new UnauthorisedException();
    }
    int loginValid = Integer.parseInt(System.getProperty("loginValid"));
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_YEAR,loginValid);
    String authId = UUID.randomUUID().toString();
    auth.setAuthtoken(authId);
    auth.setValid(cal.getTime());
    authenticateDao.createUpdate(auth);
    return auth;



  }
}
