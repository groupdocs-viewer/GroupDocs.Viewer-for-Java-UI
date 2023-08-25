package com.groupdocs.viewerui.ui.api.models;

public class FileResponse {
    public final byte[] data;
    public final String fileName;

    public FileResponse(byte[] data, String fileName) {
        this.data = data;
        this.fileName = fileName;
    }
}
