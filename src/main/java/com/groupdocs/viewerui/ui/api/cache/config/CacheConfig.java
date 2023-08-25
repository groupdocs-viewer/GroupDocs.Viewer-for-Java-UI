package com.groupdocs.viewerui.ui.api.cache.config;

public class CacheConfig {
    private int _cacheEntryExpirationTimeoutMinutes = 0;

    private boolean _groupCacheEntriesByFile;

    /**
     * Set grouping cache entries by file. When enabled eviction of any cache entry leads to eviction of all this file cache entries.
     * This setting has effect only when <see cref="CacheEntryExpirationTimeoutMinutes"/> is greater than zero.
     *
     * @return This instance.
     */
    public boolean getGroupCacheEntriesByFile() {
        return _groupCacheEntriesByFile;
    }

    /**
     * Set grouping cache entries by file. When enabled eviction of any cache entry leads to eviction of all this file cache entries.
     * This setting has effect only when <see cref="CacheEntryExpirationTimeoutMinutes"/> is greater than zero.
     *
     * @param groupCacheEntriesByFile true to enable grouping.
     */
    public void setGroupCacheEntriesByFile(boolean groupCacheEntriesByFile) {
        this._groupCacheEntriesByFile = groupCacheEntriesByFile;
    }

    /**
     * Set the expiration timeout of each cache entry in minutes.
     * The default value instanceof 0 which means that a cache entry never expires.
     *
     * @return This instance.
     */
    public int getCacheEntryExpirationTimeoutMinutes() {
        return _cacheEntryExpirationTimeoutMinutes;
    }

    /**
     * Set the expiration timeout of each cache entry in minutes.
     * The default value instanceof 0 which means that a cache entry never expires.
     *
     * @param cacheEntryExpirationTimeoutMinutes The expiration timeout in minutes.
     */
    public void setCacheEntryExpirationTimeoutMinutes(int cacheEntryExpirationTimeoutMinutes) {
        this._cacheEntryExpirationTimeoutMinutes = cacheEntryExpirationTimeoutMinutes;
    }
}
