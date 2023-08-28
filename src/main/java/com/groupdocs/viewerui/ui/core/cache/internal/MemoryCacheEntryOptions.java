package com.groupdocs.viewerui.ui.core.cache.internal;

import java.util.function.Consumer;

public class MemoryCacheEntryOptions {
    private long _slidingExpiration;
    private Consumer<Object> _postEvictionCallback = null;

    public void registerPostEvictionCallback(Consumer<Object> postEvictionCallback) {
        _postEvictionCallback = postEvictionCallback;
    }

    public Consumer<Object> getPostEvictionCallback() {
        return _postEvictionCallback;
    }

    public long getSlidingExpiration() {
        return _slidingExpiration;
    }

    public void setSlidingExpiration(long slidingExpiration) {
        this._slidingExpiration = slidingExpiration;
    }
}
