package com.groupdocs.viewerui.ui.core;

/**
 * Class ActionName represents a set of predefined actions (request types).
 * Each action has a corresponding URL associated with it.
 * @author liosha
 */
public enum ActionName {
    UI_RESOURCE("/"),
    LOAD_CONFIG("/viewer-config"),
    API_LOAD_FILE_TREE("/loadFileTree"),
    API_LOAD_DOCUMENT_DESCRIPTION("/loadDocumentDescription"),
    API_LOAD_DOCUMENT_PAGE_RESOURCE("/loadDocumentPageResource"),
    API_DOWNLOAD_DOCUMENT("/downloadDocument"),
    API_UPLOAD_DOCUMENT("/uploadDocument"),
    API_LOAD_DOCUMENT_PAGES("/loadDocumentPages"),
    API_PRINT_PDF("/printPdf"),
/*
    API_LOAD_DOCUMENT_PAGE(""),
    API_LOAD_THUMBNAILS(""),*/;

    private String _url;

    private ActionName(String url) {
        this._url = url;
    }

    public String getUrl() {
        return _url;
    }
}
