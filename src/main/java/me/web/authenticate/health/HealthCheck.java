package me.web.authenticate.health;

/**
 * Created by craigbrookes on 13/03/15.
 */
public class HealthCheck extends com.codahale.metrics.health.HealthCheck {
  @Override
  protected Result check() throws Exception {
    return Result.healthy();
  }
}
