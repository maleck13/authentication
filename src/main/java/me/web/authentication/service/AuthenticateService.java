package me.web.authentication.service;


import me.web.authentication.core.Authentication;
import me.web.authentication.dao.AuthenticateDao;
import me.web.authentication.exceptions.ResourceConflictException;
import me.web.authentication.exceptions.UnauthorisedException;
import org.hibernate.exception.ConstraintViolationException;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.ws.rs.WebApplicationException;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AuthenticateService {

  private AuthenticateDao authenticateDao;


  Logger authLog = LoggerFactory.getLogger(AuthenticateService.class);

  public AuthenticateService(final AuthenticateDao authenticateDao) {
    this.authenticateDao = authenticateDao;
  }

  public Authentication findUser(String id){
    authLog.info("finding user " + id);
    Authentication auth = authenticateDao.findByUserId(id);
    if(null == auth){
      authLog.info("failed to find by user id "+id+ " checking by authtoken");
      auth = authenticateDao.findUserByAuthToken(id);
    }
    return auth;
  }

  public List<Authentication> authenticationList(){
    return authenticateDao.findAll();
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
      throw new ResourceConflictException();
    }catch (Exception  ex){
      throw new WebApplicationException(500);
    }
  }

  public void delete(Authentication authentication){

  }

  public void invalidate(String userId, String token){
    if(null != userId && null != token){
      Authentication auth = findUser(userId);
      if(auth.getAuthtoken().equals(token)){
        auth.setAuthtoken(null);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        auth.setValid(cal.getTime());
      }
    }
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
      authLog.info(" failed to find user ");
      throw new UnauthorisedException();
    }

    authLog.info("validation user id " + auth.getUserid() + " " + authentication.getUserid());
    valid = (auth.getUserid().equals(authentication.getUserid()));

    if(valid && null != authentication.getPassword()){
      authLog.info("validating password ");
        valid = (BCrypt.checkpw(getPass(authentication.getPassword()),auth.getPassword()));
      //no password sent check temp auth token
    }else if(valid && null != authentication.getAuthtoken()){
      authLog.info("validation auth token  " + auth.getAuthtoken() + " " + authentication.getAuthtoken());
      valid = (authentication.getAuthtoken().equals(auth.getAuthtoken()));
      valid = (valid && null != auth.getValid() && auth.getValid().getTime() > now.getTime());
    }

    if(!valid){
      throw new UnauthorisedException("incorrect username password");
    }
    int loginValid = Integer.parseInt(System.getProperty("loginValid"));
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_YEAR,loginValid);
    if(null == authentication.getAuthtoken()) {
      String authId = UUID.randomUUID().toString();
      auth.setAuthtoken(authId);
    }

    auth.setValid(cal.getTime());
    authenticateDao.createUpdate(auth);
    return auth;



  }
}
