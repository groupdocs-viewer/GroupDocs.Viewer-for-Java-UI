package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatePdfResponse {

    @JsonProperty("pdfUrl")
    private String _pdfUrl;

    /**
     * Url to download PDF file.
     */
    public String getPdfUrl() {
        return _pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        _pdfUrl = pdfUrl;
    }
}
