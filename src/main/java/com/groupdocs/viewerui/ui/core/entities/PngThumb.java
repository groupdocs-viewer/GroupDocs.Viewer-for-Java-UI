package com.groupdocs.viewerui.ui.core.entities;

public class PngThumb extends Thumb {
    public static final String DEFAULT_EXTENSION = ".png";

    public PngThumb(int pageNumber, byte[] thumbData) {
        super(pageNumber, thumbData);
    }

    @Override
    public String getExtension() {
        return DEFAULT_EXTENSION;
    }

    @Override
    public String getContentType() {
        return "image/png";
    }
}