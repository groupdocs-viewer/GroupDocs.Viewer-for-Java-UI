package com.groupdocs.viewerui.ui.api.cache;

import com.groupdocs.viewerui.ui.api.cache.config.CacheConfig;
import com.groupdocs.viewerui.ui.core.IFileCache;

import java.util.HashMap;
import java.util.Map;

public class InMemoryFileCache implements IFileCache {
    private final Map<String, CacheEntry<Object>> _cache;
    private final CacheConfig _config;

    public InMemoryFileCache(CacheConfig config) {
        _cache = new HashMap<>();
        _config = config;
    }

    public <TEntry> TEntry get(String cacheKey, String filePath) {
        String key = filePath + "_" + cacheKey;
        if (_cache.containsKey(key)) {
            final CacheEntry<Object> objectCacheEntry = _cache.get(key);
            if (objectCacheEntry == null) {
                return null;
            }
            long cacheEntryExpirationTimeout = _config.getCacheEntryExpirationTimeoutMinutes() * 60 * 1000L;
            if (objectCacheEntry.creationTime + cacheEntryExpirationTimeout < System.currentTimeMillis()) {
                _cache.remove(key);
                return null;
            } else {
                return (TEntry) objectCacheEntry.value;
            }
        }

        return null;
    }

    public void set(String cacheKey, String filePath, Object entry) {
//        MemoryCacheEntryOptions entryOptions;

        if (_config.getCacheEntryExpirationTimeoutMinutes() > 0) {
            throw new IllegalStateException("Not implemented");
//            var cts = GetOrCreateCancellationTokenSource(cacheKey, filePath);
//            entryOptions = CreateCacheEntryOptions(cts);
        } else {
//            entryOptions = createCacheEntryOptions();
        }

        String key = filePath + "_" + cacheKey;
        _cache.put(key, new CacheEntry<>(entry)/*, entryOptions*/);
    }

    static class CacheEntry<T> {
        public final long creationTime;
        public final T value;

        public CacheEntry(T value) {
            this.creationTime = System.currentTimeMillis();
            this.value = value;
        }
    }
}
