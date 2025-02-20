package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadFileResponse {

    @JsonProperty("file")
    private final String _file;

    /**
     * .ctor
     */
    public UploadFileResponse(String filePath) {
        _file = filePath;
    }

    /**
     * Unique file ID.
     */
    public String getFile() {
        return _file;
    }

}
