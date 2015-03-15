package me.web.authenticate;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by craigbrookes on 15/03/15.
 */
public class RedisBundle<T extends Configuration> implements ConfiguredBundle<T> {

  private JedisPool jedisPool;


  @Override
  public void run(T config, Environment environment) throws Exception {
    System.out.print("starting redis");
    AuthenticateConfiguration authenticateConfiguration = (AuthenticateConfiguration) config;
    RedisConfiguration redisConfiguration = authenticateConfiguration.getRedis();
    jedisPool = new JedisPool(new JedisPoolConfig(),redisConfiguration.getHostname(),redisConfiguration.getPort(),2000,redisConfiguration.getPassword());

  }

  @Override
  public void initialize(Bootstrap<?> bootstrap) {

  }

  public JedisPool getJedisPool(){
    return jedisPool;
  }
}
