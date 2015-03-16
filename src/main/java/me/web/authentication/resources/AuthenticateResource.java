package me.web.authentication.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.UnitOfWork;
import me.web.authentication.core.Authentication;
import me.web.authentication.exceptions.ResourceNotFoundException;
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


@Path("/authentication")
@Produces(MediaType.APPLICATION_JSON)
public class AuthenticateResource {

  private final AuthenticateService authenticateService;
  private static final String XAUTH_HEADER = "x-auth";

  public AuthenticateResource(AuthenticateService authenticateService) {
    this.authenticateService = authenticateService;
  }

  @GET
  @Timed
  @UnitOfWork
  public Authentication get(@QueryParam("name") Optional<String> name) {
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
  public void validate(@Context HttpServletRequest req, @Context HttpServletResponse res){
    Cookie auth = AuthenticateService.getAuthCookie(req.getCookies());
    String authToken;
    if(null != auth){
      authToken = auth.getValue();
    }else{
      authToken = req.getHeader(XAUTH_HEADER);
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
    }
   Cookie c = authenticateService.validate(authentication);
    res.addCookie(c);

  }
}
