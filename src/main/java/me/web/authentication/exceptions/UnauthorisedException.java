package me.web.authentication.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class UnauthorisedException extends WebApplicationException {

  public UnauthorisedException() {
    super(Response.status(Response.Status.UNAUTHORIZED)
        .entity("{\"error\":\"unauthorised\"}").type(MediaType.APPLICATION_JSON).build());
  }

  public UnauthorisedException(String message) {

    super(Response.status(Response.Status.UNAUTHORIZED)
        .entity("{\"error\":"+message+"}").type(MediaType.APPLICATION_JSON).build());
  }
}
