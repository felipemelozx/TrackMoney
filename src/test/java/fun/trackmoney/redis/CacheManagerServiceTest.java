package fun.trackmoney.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CacheManagerServiceTest {

    private CacheManager cacheManager;
    private Cache cache;
    private CacheManagerService cacheManagerService;

    @BeforeEach
    void setUp() {
        cacheManager = Mockito.mock(CacheManager.class);
        cache = Mockito.mock(Cache.class);
        cacheManagerService = new CacheManagerService(cacheManager);
    }

    @Test
    void put_ShouldReturnTrue_WhenCacheExists() {
        when(cacheManager.getCache("testCache")).thenReturn(cache);

        boolean result = cacheManagerService.put("testCache", "key1", "value1");

        assertTrue(result);
        verify(cache).put("key1", "value1");
    }

    @Test
    void put_ShouldReturnFalse_WhenCacheDoesNotExist() {
        when(cacheManager.getCache("testCache")).thenReturn(null);

        boolean result = cacheManagerService.put("testCache", "key1", "value1");

        assertFalse(result);
        verify(cache, never()).put(any(), any());
    }

    @Test
    void get_ShouldReturnValue_WhenKeyExists() {
        when(cacheManager.getCache("testCache")).thenReturn(cache);
        Cache.ValueWrapper wrapper = mock(Cache.ValueWrapper.class);
        when(cache.get("key1")).thenReturn(wrapper);
        when(wrapper.get()).thenReturn("value1");

        String result = cacheManagerService.get("testCache", "key1", String.class);

        assertEquals("value1", result);
    }

    @Test
    void get_ShouldReturnNull_WhenCacheDoesNotExist() {
        when(cacheManager.getCache("testCache")).thenReturn(null);

        String result = cacheManagerService.get("testCache", "key1", String.class);

        assertNull(result);
    }

    @Test
    void get_ShouldReturnNull_WhenKeyDoesNotExist() {
        when(cacheManager.getCache("testCache")).thenReturn(cache);
        when(cache.get("key1")).thenReturn(null);

        String result = cacheManagerService.get("testCache", "key1", String.class);

        assertNull(result);
    }

    @Test
    void evict_ShouldCallEvict_WhenCacheExists() {
        when(cacheManager.getCache("testCache")).thenReturn(cache);

        cacheManagerService.evict("testCache", "key1");

        verify(cache).evict("key1");
    }

    @Test
    void evict_ShouldDoNothing_WhenCacheDoesNotExist() {
        when(cacheManager.getCache("testCache")).thenReturn(null);

        cacheManagerService.evict("testCache", "key1");

        verify(cache, never()).evict(any());
    }

    @Test
    void clear_ShouldCallClear_WhenCacheExists() {
        when(cacheManager.getCache("testCache")).thenReturn(cache);

        cacheManagerService.clear("testCache");

        verify(cache).clear();
    }

    @Test
    void clear_ShouldDoNothing_WhenCacheDoesNotExist() {
        when(cacheManager.getCache("testCache")).thenReturn(null);

        cacheManagerService.clear("testCache");

        verify(cache, never()).clear();
    }
}
