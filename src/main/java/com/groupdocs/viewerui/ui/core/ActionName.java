package com.groupdocs.viewerui.ui.core;

/**
 * Class ActionName represents a set of predefined actions (request types).
 * Each action has a corresponding URL associated with it.
 * @author liosha
 */
public enum ActionName {
    UI_RESOURCE("/"),
    API_METHOD_LIST_DIR("/list-dir"),
    API_METHOD_UPLOAD_FILE("/upload-file"),
    API_METHOD_VIEW_DATA("/view-data"),
    API_METHOD_CREATE_PAGES("/create-pages"),
    API_METHOD_CREATE_PDF("/create-pdf"),
    API_METHOD_GET_PAGE("/get-page"),
    API_METHOD_GET_THUMB("/get-thumb"),
    API_METHOD_GET_PDF("/get-pdf"),
    API_METHOD_GET_RESOURCE("/get-resource");

    private String _url;

    private ActionName(String url) {
        this._url = url;
    }

    public String getUrl() {
        return _url;
    }
}
