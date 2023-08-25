package com.groupdocs.viewerui.ui.api.internalcaching;

import com.groupdocs.viewer.Viewer;
import com.groupdocs.viewerui.ui.configuration.InternalCacheOptions;
import com.groupdocs.viewerui.ui.core.cache.MemoryCache;
import com.groupdocs.viewerui.ui.core.cache.MemoryCacheEntryOptions;
import com.groupdocs.viewerui.ui.core.entities.FileCredentials;

public class InMemoryInternalCache implements InternalCache {

    private final MemoryCache _cache;
    private final InternalCacheOptions _options;

    public InMemoryInternalCache(MemoryCache memoryCache, InternalCacheOptions options) {
        _cache = memoryCache;
        _options = options;
    }

    public Viewer get(FileCredentials fileCredentials) {
        final String key = setKey(fileCredentials);
        final Viewer viewer = _cache.get(key);
        if (viewer == null) {
            return null;
        }
        return viewer;
    }

    public void set(FileCredentials fileCredentials, Viewer entry) {
        String entryKey = setKey(fileCredentials);
        final Viewer viewer = _cache.get(entryKey);
        if (viewer == null) {
            MemoryCacheEntryOptions entryOptions = createCacheEntryOptions();
            _cache.put(entryKey, entry, entryOptions);
        }
    }

    private String setKey(FileCredentials fileCredentials) {
        return fileCredentials.getFilePath() + "_" + fileCredentials.getPassword() + "__VC";
    }

    private MemoryCacheEntryOptions createCacheEntryOptions() {
        MemoryCacheEntryOptions entryOptions = new MemoryCacheEntryOptions();

        if (_options.getCacheEntryExpirationTimeoutMinutes() > 0) {
            entryOptions.setSlidingExpiration(_options.getCacheEntryExpirationTimeoutMinutes() * 60 * 1000L);
        }

        entryOptions.registerPostEvictionCallback(viewer -> {
            if (viewer instanceof Viewer) {
                ((Viewer) viewer).close();
            }
        });

        return entryOptions;
    }

    public InternalCacheOptions getOptions() {
        return _options;
    }

}
