package com.groupdocs.viewerui.ui.core.entities;

public class JpgThumb extends Thumb {

    public JpgThumb(int pageNumber, byte[] thumbData) {
        super(pageNumber, thumbData);
    }

    public static final String DEFAULT_EXTENSION = ".jpeg";

    @Override
    public String getExtension() {
        return DEFAULT_EXTENSION;
    }

    @Override
    public String getContentType() {
        return "image/jpeg";
    }
}
