package fun.trackmoney.infra.cache;

import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CacheConfigTest {

  @Test
  void cacheManager_ShouldReturnConfiguredCaffeineCacheManager() {
    CacheConfig cacheConfig = new CacheConfig();
    CacheManager cacheManager = cacheConfig.cacheManager();

    assertNotNull(cacheManager);
    assertInstanceOf(CaffeineCacheManager.class, cacheManager);

    Cache cache = cacheManager.getCache("EmailVerificationCodes");
    assertNotNull(cache);

    cache.put("user@example.com", "123456");
    assertEquals("123456", cache.get("user@example.com", String.class));
  }
}
