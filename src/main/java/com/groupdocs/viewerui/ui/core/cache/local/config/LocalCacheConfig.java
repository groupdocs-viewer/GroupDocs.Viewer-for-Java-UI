package com.groupdocs.viewerui.ui.core.cache.local.config;

import java.nio.file.Path;

public class LocalCacheConfig {
    private Path _cachePath;

    /**
     * Relative or absolute path where document cache will be stored.
     * @return path where document cache will be stored
     */
    public Path getCachePath() {
        return _cachePath;
    }

    public void setCachePath(Path cachePath) {
        this._cachePath = cachePath;
    }

    @Override
    public String toString() {
        return "LocalCacheConfig {" +
               " cachePath=" + _cachePath +
               " }";
    }
}
