package me.web.authentication.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by craigbrookes on 16/03/15.
 */
public class ResourceNotFoundException extends WebApplicationException {
  public ResourceNotFoundException() {
    super(Response.status(Response.Status.NOT_FOUND)
        .entity("{\"error\":\"not found\"}").type(MediaType.APPLICATION_JSON).build());
  }
}
