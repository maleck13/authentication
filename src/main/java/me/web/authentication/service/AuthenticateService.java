package me.web.authentication.service;


import io.dropwizard.hibernate.UnitOfWork;
import me.web.authentication.core.Authentication;
import me.web.authentication.dao.AuthenticateDao;
import me.web.authentication.exceptions.UnauthorisedException;
import me.web.authentication.json.util.JsonUtil;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Calendar;
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
    Authentication auth = authenticateDao.findByUserId(id);
    if(null == auth){
      auth = authenticateDao.findUserByAuthToken(id);
    }
    return auth;
  }

  public String hashPassword(String pass)throws Exception{
    String salt = System.getProperty("salt");
    MessageDigest m = MessageDigest.getInstance("SHA-256");
    String sb = salt + pass + salt + salt;
    m.update(sb.getBytes());
    return javax.xml.bind.DatatypeConverter.printHexBinary(m.digest());
  }

  public Authentication setUp(Authentication authentication){
    try {
      String hashed =  hashPassword(authentication.getPassword());
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
    for(Cookie c : cookies){
      if(c.getName().equals("auth")){
        return c;
      }
    }
    return null;
  }

  public Cookie validate(Authentication authentication){
    Authentication auth = null;
    Cookie c;
    if(null != authentication.getUserid() && null != authentication.getPassword()) {
      auth = findUser(authentication.getUserid());
    }else if(null != authentication.getAuthtoken()){
      try {
        Jedis redis = jedisPool.getResource();
        String redisSession = redis.get(authentication.getAuthtoken());
        if(null != redisSession) {
          auth = JsonUtil.fromJSON(redisSession);
          redis.setex(authentication.getAuthtoken(), 60 * 60, JsonUtil.toJSON(auth));
          c = new Cookie("auth", authentication.getAuthtoken());
          return c;
        }

      }catch (Exception e){
        authLog.warn("failed redis or json issue ",e);
        throw new UnauthorisedException(); //just get them to relogin
      }
    }

    if(null == auth){
      throw new UnauthorisedException();
    }
    String hashed;
    try {
      hashed = hashPassword(authentication.getPassword());
    }catch (Exception e){
      throw new WebApplicationException(500);
    }
    if(auth.getPassword().equals(hashed) && auth.getUserid().equals(authentication.getUserid())){
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DAY_OF_YEAR,5);
      String authId = UUID.randomUUID().toString();
      c = new Cookie("auth",authId);
      auth.setAuthtoken(authId);
      auth.setValid(cal.getTime());
      authenticateDao.createUpdate(auth);
      try {
        jedisPool.getResource().setex(authId, 60 * 60, JsonUtil.toJSON(auth));
      }catch (IOException e){
        authLog.warn("error resetting session ", e);
        throw new WebApplicationException(500);
      }
      return c;
    }
    else{
      throw new UnauthorisedException();
    }
  }
}
