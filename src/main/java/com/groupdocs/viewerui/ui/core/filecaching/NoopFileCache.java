package com.groupdocs.viewerui.ui.core.filecaching;

import com.groupdocs.viewerui.ui.api.cache.IFileCache;

public class NoopFileCache implements IFileCache {

    @Override
    public <TEntry> TEntry get(String cacheKey, String filePath) {
        return null;
    }

    @Override
    public <TEntry> void set(String cacheKey, String filePath, TEntry tEntry) {

    }
}