package me.web.authentication.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.UnitOfWork;
import me.web.authentication.core.Authentication;
import me.web.authentication.exceptions.ResourceNotFoundException;
import me.web.authentication.exceptions.UnauthorisedException;
import me.web.authentication.service.AuthenticateService;
import org.hibernate.exception.ConstraintViolationException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;


@Path("/authentication")
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticateResource {

  private final AuthenticateService authenticateService;
  private static final String XAUTH_HEADER = "x-auth";
  private static final String XAUTH_ID_HEADER = "x-authid";

  public AuthenticateResource(AuthenticateService authenticateService) {
    this.authenticateService = authenticateService;
  }

  @GET
  @Timed
  @UnitOfWork
  public Authentication get(@QueryParam("userid") Optional<String> name) {
   if(!name.isPresent()){
     throw new WebApplicationException("userid required",400);
   }
   Authentication auth =  authenticateService.findUser(name.get());
    if(null == auth){
      throw new ResourceNotFoundException();
    }
    return auth;
  }

  @POST
  @Timed
  @UnitOfWork
  public Authentication set(@Valid Authentication authentication, @Context HttpServletResponse res){
      return authenticateService.setUp(authentication);
  }

  @DELETE
  @Timed
  @UnitOfWork
  @Path("{id}")
  public void delete(@PathParam("id") String id){
    Authentication authentication = authenticateService.findUser(id);
    if(null == authentication){
      throw  new ResourceNotFoundException();
    }
    authenticateService.delete(authentication);
  }

  @POST
  @Path("/validate")
  @Timed
  @UnitOfWork
  public Authentication validate(@Context HttpServletRequest req, @Context HttpServletResponse res){
    Cookie auth = AuthenticateService.getAuthCookie(req.getCookies());
    String authToken;
    String userId;
    if(null != auth){
      String userAndAuthToken = auth.getValue();
      String[] userAuth =userAndAuthToken.split(":");
      if(userAuth.length < 2){
        throw new UnauthorisedException("invalid");
      }
      userId = userAuth[0];
      authToken = userAuth[1];
    }else{
      authToken = req.getHeader(XAUTH_HEADER);
      userId = req.getHeader(XAUTH_ID_HEADER);
    }
    Authentication authentication = new Authentication();
    if(null == authToken){
      ObjectMapper mapper = new ObjectMapper();
      try {
        authentication = mapper.readValue(req.getInputStream(), Authentication.class);
      }catch (IOException e){
        throw new WebApplicationException(401);
      }
    }else{
      authentication.setAuthtoken(authToken);
      authentication.setUserid(userId);
    }
    Authentication retAuth = authenticateService.validate(authentication);
    auth = new Cookie("auth",retAuth.getUserid() + ":" + retAuth.getAuthtoken());
    res.addCookie(auth);
    return retAuth;
  }
}
