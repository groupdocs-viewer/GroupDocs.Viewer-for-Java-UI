package com.groupdocs.viewerui.ui.core.caching;

public class CachedPage {
    /**
     * The page number.
     */
    private final int _pageNumber;
    /**
     * The data. Can be null.
     */
    private final byte[] _data;

    public CachedPage(int pageNumber, byte[] data) {
        _pageNumber = pageNumber;
        _data = data;
    }

    public int getPageNumber() {
        return _pageNumber;
    }

    public byte[] getData() {
        return _data;
    }
}
