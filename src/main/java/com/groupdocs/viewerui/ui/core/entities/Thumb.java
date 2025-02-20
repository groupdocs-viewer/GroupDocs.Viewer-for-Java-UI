package com.groupdocs.viewerui.ui.core.entities;

public abstract class Thumb {

    private int _pageNumber;
    private byte[] _thumbData;

    protected Thumb(int pageNumber, byte[] thumbData) {
        this._pageNumber = pageNumber;
        this._thumbData = thumbData;
    }

    public int getPageNumber() {
        return _pageNumber;
    }

    public byte[] getThumbData() {
        return _thumbData;
    }

    protected void setThumbData(byte[] thumbData) {
        this._thumbData = thumbData;
    }

    public abstract String getExtension();

    public abstract String getContentType();
}
