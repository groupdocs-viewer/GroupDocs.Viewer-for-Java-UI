package com.groupdocs.viewerui.ui.api.cache;

import com.groupdocs.viewerui.ui.core.cache.NoopFileCache;

import java.util.function.Supplier;

public class FileCacheFactory {
    private static Supplier<FileCache> _fileCacheSupplier;

    public static synchronized FileCache newInstance() {
        if (_fileCacheSupplier == null) {
            _fileCacheSupplier = NoopFileCache::new;
        }
        return _fileCacheSupplier.get();
    }

    public static void setSupplier(Supplier<FileCache> fileCacheSupplier) {
        FileCacheFactory._fileCacheSupplier = fileCacheSupplier;
    }

    public static boolean isSupplierPresent() {
        return _fileCacheSupplier != null;
    }
}
