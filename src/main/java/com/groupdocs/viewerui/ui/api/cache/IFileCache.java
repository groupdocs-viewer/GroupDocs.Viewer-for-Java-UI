package com.groupdocs.viewerui.ui.api.cache;

public interface IFileCache {
    <TEntry> TEntry get(String cacheKey, String filePath);

    <TEntry> void set(String cacheKey, String filePath, TEntry entry);
}
