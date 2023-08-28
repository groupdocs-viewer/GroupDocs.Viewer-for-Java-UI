package com.groupdocs.viewerui.ui.core.cache.internal;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

// TODO: Not tested at all
public class DefaultMemoryCache implements MemoryCache {
    private final MemoryCacheOptions _memoryCacheOptions;
    private Map<String, CacheEntry> _cache = new ConcurrentHashMap<>();
    private long _lastExpirationScan = 0L;

    public DefaultMemoryCache() {
        this._memoryCacheOptions = new MemoryCacheOptions();
    }

    public DefaultMemoryCache(MemoryCacheOptions memoryCacheOptions) {
        this._memoryCacheOptions = memoryCacheOptions;
    }

    public MemoryCacheOptions getMemoryCacheOptions() {
        return _memoryCacheOptions;
    }

    @Override
    public <T> T get(String key) {
        startScanForExpiredItemsIfNeeded();
        final CacheEntry cacheEntry = _cache.get(key);
        if (cacheEntry == null) {
            return null;
        }
        cacheEntry.updateLastAccess();
        return (T) cacheEntry.value;
    }

    private void startScanForExpiredItemsIfNeeded() {
        final long currentTimeMillis = System.currentTimeMillis();
        if (this._memoryCacheOptions.getExpirationScanFrequency() < currentTimeMillis - _lastExpirationScan) {
            _lastExpirationScan = currentTimeMillis;
            scanForExpiredItems();
        }
    }

    private void scanForExpiredItems() {
        final Iterator<Map.Entry<String, CacheEntry>> iterator = _cache.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, CacheEntry> next = iterator.next();
            final CacheEntry value = next.getValue();
            if (value.checkExpired(System.currentTimeMillis())) {
                iterator.remove();
                final MemoryCacheEntryOptions cacheEntryOptions = value.getCacheEntryOptions();
                final Consumer<Object> postEvictionCallback = cacheEntryOptions.getPostEvictionCallback();
                if (postEvictionCallback != null) {
                    postEvictionCallback.accept(value);
                }
            }
        }
    }

    @Override
    public <T> void put(String entryKey, T value, MemoryCacheEntryOptions memoryCacheEntryOptions) {
        final CacheEntry cacheEntry = new CacheEntry(value, memoryCacheEntryOptions);
        final CacheEntry oldEntry = _cache.put(entryKey, cacheEntry);
        if (oldEntry != null && oldEntry.getCacheEntryOptions() != null) {
            final Consumer<Object> postEvictionCallback = oldEntry.getCacheEntryOptions().getPostEvictionCallback();
            if (postEvictionCallback != null) {
                postEvictionCallback.accept(oldEntry.getValue());
            }
        }
    }


    static class CacheEntry {
        private final MemoryCacheEntryOptions _cacheEntryOptions;
        private final Object value;
        private long _lastAccess = 0L;

        public CacheEntry(Object value, MemoryCacheEntryOptions cacheEntryOptions) {
            this._cacheEntryOptions = cacheEntryOptions;
            this.value = value;
        }

        boolean checkExpired(long now) {
            if (_lastAccess != 0 && now > (_lastAccess + _cacheEntryOptions.getSlidingExpiration())) {
                return true;
            }
            return false;
        }

        void updateLastAccess() {
            _lastAccess = System.currentTimeMillis();
        }

        public Object getValue() {
            return value;
        }

        public MemoryCacheEntryOptions getCacheEntryOptions() {
            return _cacheEntryOptions;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CacheEntry that = (CacheEntry) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}
