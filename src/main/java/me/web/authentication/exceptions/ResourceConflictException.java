package me.web.authentication.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by craigbrookes on 06/04/15.
 */
public class ResourceConflictException extends WebApplicationException {
  public ResourceConflictException() {
    super(Response.status(Response.Status.CONFLICT)
        .entity("{\"error\":\"conflict\"}").type(MediaType.APPLICATION_JSON).build());
  }
}
