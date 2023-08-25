package com.groupdocs.viewerui.ui.core.cache;

public class MemoryCacheFactory {
    private static MemoryCache _memoryCache;

    public static final MemoryCache getInstance() {
        if (_memoryCache == null) {
            _memoryCache = new DefaultMemoryCache();
        }
        return _memoryCache;
    }

    public static void setInstance(MemoryCache memoryCache) {
        MemoryCacheFactory._memoryCache = memoryCache;
    }
}
