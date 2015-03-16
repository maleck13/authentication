import io.dropwizard.testing.junit.ResourceTestRule;
import me.web.authentication.core.Authentication;
import me.web.authentication.resources.AuthenticateResource;
import me.web.authentication.service.AuthenticateService;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import redis.clients.jedis.JedisPool;
import static org.assertj.core.api.Assertions.assertThat;

import static  org.mockito.Mockito.*;


public class TestAuthenticationResource {

  private static final AuthenticateService authService = mock(AuthenticateService.class);
  private Authentication auth = new Authentication();

  @ClassRule
  public static final ResourceTestRule resources = ResourceTestRule.builder()
      .addResource(new AuthenticateResource(authService))
      .build();

  @Before
  public void setUp(){
    auth.setPassword("password");
    auth.setUserid("test@test.com");
    auth.setAuthtoken("testtoken");
    when(authService.findUser(anyString())).thenReturn(auth);

  }


  @Test
  public void testGetPerson() {
    assertThat(resources.client().target("/authentication?name=test@test.com").request().get(Authentication.class))
        .isEqualTo(auth);
    verify(authService).findUser("test@test.com");
  }

}
