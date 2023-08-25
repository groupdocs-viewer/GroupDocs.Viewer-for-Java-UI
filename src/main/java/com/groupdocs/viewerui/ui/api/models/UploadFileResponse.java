package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadFileResponse {

    @JsonProperty("guid")
    private final String _guid;

    /**
     * .ctor
     */
    public UploadFileResponse(String filePath) {
        _guid = filePath;
    }

    /**
     * Unique file ID.
     */
    public String getGuid() {
        return _guid;
    }

}
