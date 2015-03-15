package me.web.authenticate.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.UnitOfWork;
import me.web.authenticate.core.Authenticate;
import me.web.authenticate.dao.AuthenticateDao;
import me.web.authenticate.service.AuthenticateService;
import redis.clients.jedis.JedisPool;

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
  public Authenticate get(@QueryParam("name") Optional<String> name) {
   Authenticate auth =  authenticateService.findUser(name.get());
    if(null == auth){
      throw new NotFoundException();
    }
    return auth;
  }

  @POST
  @Timed
  @UnitOfWork
  public Authenticate set(@Valid Authenticate authenticate, @Context HttpServletResponse res){
    return authenticateService.setUp(authenticate);
  }

  @POST
  @Path("/validate")
  @Timed
  @UnitOfWork
  public void validate(@Context HttpServletRequest req, @Context HttpServletResponse res){
    String authToken = req.getHeader(XAUTH_HEADER);
    Authenticate authenticate = new Authenticate();
    if(null == authToken){
      ObjectMapper mapper = new ObjectMapper();
      try {
        authenticate = mapper.readValue(req.getInputStream(), Authenticate.class);
      }catch (IOException e){
        throw new WebApplicationException(400);
      }
    }else{
      authenticate.setAuthtoken(authToken);
    }
   Cookie c = authenticateService.validate(authenticate);
    res.addCookie(c);

  }
}
