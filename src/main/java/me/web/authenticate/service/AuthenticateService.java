package me.web.authenticate.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import me.web.authenticate.core.Authenticate;
import me.web.authenticate.dao.AuthenticateDao;
import me.web.authenticate.json.util.JsonUtil;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
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

  public Authenticate findUser(String id){
    Authenticate auth = authenticateDao.findByUserId(id);
    if(null == auth){
      auth = authenticateDao.findUserByAuthToken(id);
    }
    return auth;
  }

  public Authenticate setUp(Authenticate authenticate){
    try {
      return authenticateDao.createUpdate(authenticate);
    }catch (HibernateException e){
      throw new WebApplicationException(409);
    }
  }

  public Cookie validate(Authenticate authenticate){
    Authenticate auth = null;
    Cookie c;
    if(null != authenticate.getUserid() && null != authenticate.getPassword()) {
      auth = findUser(authenticate.getUserid());
    }else if(null != authenticate.getAuthtoken()){
      try {
        Jedis redis = jedisPool.getResource();
        String redisSession = redis.get(authenticate.getAuthtoken());
        if(null != redisSession) {
          auth = JsonUtil.fromJSON(redisSession);
          redis.setex(authenticate.getAuthtoken(), 60 * 60, JsonUtil.toJSON(auth));
          c = new Cookie("auth",authenticate.getAuthtoken());
          return c;
        }

      }catch (Exception e){
        authLog.warn("failed redis or json issue ",e);
        throw new WebApplicationException(401); //just get them to relogin
      }
    }

    if(null == auth){
      throw new WebApplicationException(401);
    }

    if(auth.getPassword().equals(authenticate.getPassword())){
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
      throw new WebApplicationException(401);
    }
  }
}
