import me.web.authentication.core.Authentication;
import me.web.authentication.dao.AuthenticateDao;
import me.web.authentication.exceptions.UnauthorisedException;
import me.web.authentication.service.AuthenticateService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;

import static  org.mockito.Mockito.*;

public class TestAuthenticationService {

  private AuthenticateService authenticateService;

  private AuthenticateDao authenticateDao = mock(AuthenticateDao.class);
  private JedisPool jedisPool = mock(JedisPool.class);
  private  Jedis jedis = mock(Jedis.class);
  private Authentication auth;
  private static final String AUTH_NAME = "test@test.com";
  private static final String AUTH_TOKEN = "testtoken";

  @Before
  public void setUp(){
    authenticateService = new AuthenticateService(authenticateDao,jedisPool);
    auth = new Authentication();
    auth.setId(1);
    auth.setUserid(AUTH_NAME);
    auth.setPassword("password");
    auth.setAuthtoken(AUTH_TOKEN);
    when(jedisPool.getResource()).thenReturn(jedis);

  }

  @Test
  public void testFindUserById(){
    when(authenticateDao.findByUserId(anyString())).thenReturn(auth);
    Authentication auth = authenticateService.findUser("test@test.com");
    Assert.assertNotNull(auth);
    Assert.assertEquals(AUTH_NAME,auth.getUserid());
  }

  @Test
  public void testFindUserByToken(){
    when(authenticateDao.findByUserId(anyString())).thenReturn(null);
    when(authenticateDao.findUserByAuthToken(anyString())).thenReturn(auth);
    Authentication auth = authenticateService.findUser("sometoken");
    Assert.assertNotNull(auth);
    Assert.assertEquals(AUTH_NAME,auth.getUserid());
  }

  @Test
  public void testSetUp(){
    when(authenticateDao.createUpdate(any(Authentication.class))).thenReturn(auth);
    Authentication a = authenticateService.setUp(auth);
    Assert.assertNotNull(auth);
    Assert.assertEquals(a.getPassword(), auth.getPassword());
  }

  @Test
  public  void testValidateUserPass()throws Exception{
    Authentication authentication = new Authentication();
    authentication.setPassword(authenticateService.hashPassword(auth.getPassword()));
    authentication.setUserid(AUTH_NAME);
    when(authenticateDao.findByUserId(anyString())).thenReturn(authentication);
    Cookie c = authenticateService.validate(auth);
    Assert.assertNotNull(c);
    Assert.assertNotNull(c.getName());
    Assert.assertNotNull(c.getValue());

  }

  @Test
  public  void testValidateUserAuthToken()throws Exception{
    Authentication authentication = new Authentication();
    authentication.setAuthtoken(AUTH_TOKEN);
    when(jedis.get(anyString())).thenReturn("{}");
    when(authenticateDao.findByUserId(anyString())).thenReturn(authentication);
    Cookie c = authenticateService.validate(authentication);
    Assert.assertNotNull(c);
    Assert.assertNotNull(c.getName());
    Assert.assertNotNull(c.getValue());
    Assert.assertEquals(c.getValue(),AUTH_TOKEN);
  }


  @Test(expected = UnauthorisedException.class)
  public  void testValidateUserAuthTokenFails()throws Exception{
    Authentication authentication = new Authentication();
    authentication.setAuthtoken("invalid");
    when(authenticateDao.findByUserId(anyString())).thenReturn(authentication);
    when(jedis.get(anyString())).thenReturn(null);
    Cookie c = authenticateService.validate(authentication);
    Assert.fail();
  }

  @Test(expected = UnauthorisedException.class)
  public  void testValidateUserPassFails()throws Exception{
    Authentication authentication = new Authentication();
    authentication.setUserid(AUTH_NAME);
    authentication.setPassword("badpass");
    when(authenticateDao.findByUserId(anyString())).thenReturn(auth);
    Cookie c = authenticateService.validate(authentication);
    Assert.fail();
  }

}
