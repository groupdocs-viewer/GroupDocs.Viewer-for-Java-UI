package com.groupdocs.viewerui.ui.core;

public enum ActionName {
    UI_RESOURCE("/"),
    LOAD_CONFIG("/viewer-config"),
    API_LOAD_FILE_TREE("/loadFileTree"),
    API_LOAD_DOCUMENT_DESCRIPTION("/loadDocumentDescription"),
    API_DOWNLOAD_DOCUMENT(""),
    API_UPLOAD_DOCUMENT(""),
    API_LOAD_DOCUMENT_PAGES(""),
    API_LOAD_DOCUMENT_PAGE(""),
    API_LOAD_DOCUMENT_PAGE_RESOURCE(""),
    API_LOAD_THUMBNAILS(""),
    API_PRINT_PDF("");

    private String _url;

    private ActionName(String url) {
        this._url = url;
    }

    public String getUrl() {
        return _url;
    }
}
