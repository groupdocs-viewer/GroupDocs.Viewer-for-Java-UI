package com.groupdocs.viewerui.ui.api.cache;

import com.groupdocs.viewerui.ui.api.cache.config.CacheConfig;
import com.groupdocs.viewerui.ui.core.IFileCache;
import com.groupdocs.viewerui.ui.core.cache.MemoryCache;
import com.groupdocs.viewerui.ui.core.cache.MemoryCacheEntryOptions;

public class InMemoryFileCache implements IFileCache {
    private final MemoryCache _cache;
    private final CacheConfig _config;

    public InMemoryFileCache(MemoryCache memoryCache, CacheConfig config) {
        _cache = memoryCache;
        _config = config;
    }

    public <T> T get(String cacheKey, String filePath) {
        String key = filePath + "_" + cacheKey;
        final Object value = _cache.get(key);
        if (value != null) {
            return (T) value;
        }
        return null;
    }

    public void set(String cacheKey, String filePath, Object entry) {
        MemoryCacheEntryOptions entryOptions = new MemoryCacheEntryOptions();

        final int cacheEntryExpirationTimeoutMinutes = _config.getCacheEntryExpirationTimeoutMinutes();
        if (cacheEntryExpirationTimeoutMinutes > 0) {
            entryOptions.setSlidingExpiration(cacheEntryExpirationTimeoutMinutes * 60 * 1000L);
        }

        String key = filePath + "_" + cacheKey;
        _cache.put(key, entry, entryOptions);
    }
}
