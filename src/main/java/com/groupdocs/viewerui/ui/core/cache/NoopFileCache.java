package com.groupdocs.viewerui.ui.core.cache;

import com.groupdocs.viewerui.ui.api.cache.FileCache;

import java.io.InputStream;

public class NoopFileCache implements FileCache {


    @Override
    public <T> T get(String cacheKey, String filePath, Class<T> clazz) {
        // Noop implementation
        return null;
    }

    @Override
    public void set(String cacheKey, String filePath, byte[] value) {
        // Noop implementation
    }

    @Override
    public void set(String cacheKey, String filePath, InputStream value) {
        // Noop implementation
    }

    @Override
    public void set(String cacheKey, String filePath, Object value) {
        // Noop implementation
    }
}