package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetPdfRequest {
    @JsonProperty("file")
    private String _file;

    public GetPdfRequest() {
    }

    public GetPdfRequest(String file) {
        this._file = file;
    }

    /**
     * Unique file ID.
     */
    public String getFile() {
        return _file;
    }

    public void setFile(String file) {
        this._file = file;
    }
}
