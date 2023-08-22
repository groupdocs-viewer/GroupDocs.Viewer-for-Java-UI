package com.groupdocs.viewerui.ui.api.internalcaching;

import com.groupdocs.viewer.Viewer;
import com.groupdocs.viewerui.ui.configuration.InternalCacheOptions;
import com.groupdocs.viewerui.ui.core.entities.FileCredentials;

import java.util.HashMap;
import java.util.Map;

public class InMemoryInternalCache implements IInternalCache {

    private final Map<String, CacheEntry> _cache;
    private final InternalCacheOptions _options;

    public InMemoryInternalCache(InternalCacheOptions options) {
        _cache = new HashMap<>();
        _options = options;
    }

    public Viewer get(FileCredentials fileCredentials) {
        final String key = setKey(fileCredentials);
        final CacheEntry cacheEntry = _cache.get(key);
        if (cacheEntry == null) {
            return null;
        }
        final InternalCacheOptions options = getOptions();
        long cacheEntryExpirationTimeout = options.getCacheEntryExpirationTimeoutMinutes() * 60 * 1000L;
        if (cacheEntry.creationTime + cacheEntryExpirationTimeout < System.currentTimeMillis()) {
            _cache.remove(key);
            return null;
        } else {
            return cacheEntry.value;
        }
    }

    public void set(FileCredentials fileCredentials, Viewer entry) {
        String entryKey = setKey(fileCredentials);
        final CacheEntry oldEntry = _cache.put(entryKey, new CacheEntry(entry));
        if (oldEntry != null) {
            oldEntry.value.close();
        }
    }

    private String setKey(FileCredentials fileCredentials) {
        return fileCredentials.getFilePath() + "_" + fileCredentials.getPassword() + "__VC";
    }

    public InternalCacheOptions getOptions() {
        return _options;
    }

    static class CacheEntry {
        public final long creationTime;
        public final Viewer value;

        public CacheEntry(Viewer value) {
            this.creationTime = System.currentTimeMillis();
            this.value = value;
        }
    }
}
