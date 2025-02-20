package com.groupdocs.viewerui.ui.core.caching;

public class CacheKeys {
    public static final String FILE_INFO_CACHE_KEY = "info.json";
    public static final String PDF_FILE_CACHE_KEY = "file.pdf";

    public static String getHtmlPageResourceCacheKey(int pageNumber, String resourceName) {
        return "p" + pageNumber + "_" + resourceName;
    }

    public static String getPageCacheKey(int pageNumber, String extension) {
        return String.format("p%d%s", pageNumber, extension);
    }

    public static String getThumbCacheKey(int pageNumber, String extension) {
        return String.format("p%d_t%s", pageNumber, extension);
    }
}
