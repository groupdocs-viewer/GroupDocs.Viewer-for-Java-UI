package com.groupdocs.viewerui.ui.core.caching;

public class CachedThumb {
    private final int pageNumber;

    private final byte[] data;

    public CachedThumb(int pageNumber, byte[] data) {
        this.pageNumber = pageNumber;
        this.data = data;
    }

    /**
     * The page number.
     *
     * @return The page number.
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * The data. Can be null.
     *
     * @return The data.
     */
    public byte[] getData() {
        return data;
    }
}