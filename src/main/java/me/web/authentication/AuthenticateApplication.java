package me.web.authentication;

import de.thomaskrille.dropwizard.environment_configuration.EnvironmentConfigurationFactoryFactory;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import me.web.authentication.core.Authentication;
import me.web.authentication.dao.AuthenticateDao;
import me.web.authentication.health.HealthCheck;
import me.web.authentication.resources.AuthenticateResource;
import me.web.authentication.service.AuthenticateService;

/**
 * Created by craigbrookes on 13/03/15.
 */
public class AuthenticateApplication extends Application<AuthenticateConfiguration> {
  public static void main(String[] args) throws Exception {
    new AuthenticateApplication().run(args);
  }

  @Override
  public String getName() {
    return "authentication";
  }
  private final HibernateBundle<AuthenticateConfiguration> hibernate = new HibernateBundle<AuthenticateConfiguration>(Authentication.class) {
    @Override
    public DataSourceFactory getDataSourceFactory(AuthenticateConfiguration configuration) {
      return configuration.getDataSourceFactory();
    }
  };

  private final RedisBundle<AuthenticateConfiguration> redis = new RedisBundle<AuthenticateConfiguration>();

  @Override
  public void initialize(Bootstrap<AuthenticateConfiguration> bootstrap) {
    // nothing to do yet
    bootstrap.setConfigurationFactoryFactory(new EnvironmentConfigurationFactoryFactory<AuthenticateConfiguration>());
    bootstrap.addBundle(hibernate);
    bootstrap.addBundle(redis);
  }


  public AuthenticateDao getAuthenticateDao(){
    return new AuthenticateDao(hibernate.getSessionFactory());
  }

  public AuthenticateService getAuthenticateService(){
    return new AuthenticateService(getAuthenticateDao(),redis.getJedisPool());
  }



  @Override
  public void run(AuthenticateConfiguration configuration, Environment environment) {

    final AuthenticateResource resource = new AuthenticateResource(getAuthenticateService());
    System.setProperty("salt",configuration.getSalt());
    environment.jersey().register(resource);

    final HealthCheck health = new HealthCheck();
    environment.healthChecks().register("sys",health);

    //todo add redis health check
  }
}
