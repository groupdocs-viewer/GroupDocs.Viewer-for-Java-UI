package com.groupdocs.viewerui.ui.core.cache.memory;

import com.groupdocs.viewerui.ui.api.cache.FileCache;
import com.groupdocs.viewerui.ui.core.cache.memory.config.InMemoryCacheConfig;
import com.groupdocs.viewerui.ui.core.cache.internal.MemoryCache;
import com.groupdocs.viewerui.ui.core.cache.internal.MemoryCacheEntryOptions;

import java.io.InputStream;

public class InMemoryFileCache implements FileCache {
    private final MemoryCache _cache;
    private final InMemoryCacheConfig _config;

    public InMemoryFileCache(MemoryCache memoryCache, InMemoryCacheConfig config) {
        _cache = memoryCache;
        _config = config;
    }

    @Override
    public <T> T get(String cacheKey, String filePath, Class<T> clazz) {
        String key = filePath + "_" + cacheKey;
        final Object value = _cache.get(key);
        if (value != null) {
            return (T) value;
        }
        return null;
    }

    @Override
    public void set(String cacheKey, String filePath, byte[] value) {
        set(cacheKey, filePath, (Object) value);
    }

    @Override
    public void set(String cacheKey, String filePath, InputStream value) {
        set(cacheKey, filePath, (Object) value);
    }

    @Override
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
