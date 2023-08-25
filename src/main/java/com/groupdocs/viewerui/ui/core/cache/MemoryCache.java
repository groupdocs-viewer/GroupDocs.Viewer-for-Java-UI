package com.groupdocs.viewerui.ui.core.cache;

public interface MemoryCache {
    <T> T get(String key);

    <T> void put(String entryKey, T cacheEntry, MemoryCacheEntryOptions memoryCacheEntryOptions);
}
