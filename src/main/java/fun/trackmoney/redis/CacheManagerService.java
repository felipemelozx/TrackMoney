package fun.trackmoney.redis;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheManagerService {

  private final CacheManager cacheManager;

  public CacheManagerService(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }


  public <T> boolean put(String cacheName, Object key, T value) {
    Cache cache = cacheManager.getCache(cacheName);
    if (cache != null) {
      cache.put(key, value);
      return true;
    }
    return false;
  }

  public <T> T get(String cacheName, Object key, Class<T> type) {
    Cache cache = cacheManager.getCache(cacheName);
    if (cache == null) {
      return null;
    }
    Cache.ValueWrapper wrapper = cache.get(key);
    if (wrapper == null) {
      return null;
    }
    return type.cast(wrapper.get());
  }

  public void evict(String cacheName, Object key) {
    Cache cache = cacheManager.getCache(cacheName);
    if (cache != null) {
      cache.evict(key);
    }
  }

  public void clear(String cacheName) {
    Cache cache = cacheManager.getCache(cacheName);
    if (cache != null) {
      cache.clear();
    }
  }
}
