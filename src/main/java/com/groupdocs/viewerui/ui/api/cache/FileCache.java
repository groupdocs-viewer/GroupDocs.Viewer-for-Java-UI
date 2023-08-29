package com.groupdocs.viewerui.ui.api.cache;

import java.io.InputStream;

public interface FileCache {
    <T> T get(String cacheKey, String filePath, Class<T> clazz);

    void set(String cacheKey, String filePath, byte[] value);
    void set(String cacheKey, String filePath, InputStream value);
    void set(String cacheKey, String filePath, Object value);
}
