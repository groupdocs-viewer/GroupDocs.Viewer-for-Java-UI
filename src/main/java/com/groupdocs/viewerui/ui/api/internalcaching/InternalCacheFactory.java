package com.groupdocs.viewerui.ui.api.internalcaching;

import com.groupdocs.viewerui.ui.configuration.InternalCacheOptions;
import com.groupdocs.viewerui.ui.core.cache.internal.MemoryCache;

public class InternalCacheFactory {
    private static InternalCache _internalCache;

    public static synchronized InternalCache getInstance(MemoryCache memoryCache, InternalCacheOptions internalCacheOptions) {
        if (_internalCache == null) {
            if (internalCacheOptions.isCacheEnabled()) {
                _internalCache = new InMemoryInternalCache(memoryCache, internalCacheOptions);
            } else {
                _internalCache = new NoopInternalCache();
            }
        }
        return _internalCache;
    }

    public static void setInstance(InternalCache internalCache) {
        InternalCacheFactory._internalCache = internalCache;
    }
}
