package fun.trackmoney.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

@TestConfiguration
public class EmbeddedRedisConfig {
  private static RedisServer redisServer;

  @PostConstruct
  public static void startRedis() {
    int port = 6379;
    redisServer = new RedisServer(port);
    redisServer.start();
  }

  @PreDestroy
  public static void stopRedis(){
    if(redisServer != null){
      redisServer.stop();
    }
  }
}