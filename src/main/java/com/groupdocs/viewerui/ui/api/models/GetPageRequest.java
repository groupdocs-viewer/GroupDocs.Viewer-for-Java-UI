package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetPageRequest {
    @JsonProperty("file")
    private String _file;
    @JsonProperty("page")
    private int _page;

    /**
     * File identifier
     *
     * @return File identifier
     */
    public String getFile() {
        return _file;
    }

    /**
     * File identifier
     *
     * @param file File identifier
     */
    public void setFile(String file) {
        this._file = file;
    }

    /**
     * Page number
     *
     * @return Page number
     */
    public int getPage() {
        return _page;
    }

    /**
     * Page number
     *
     * @param page Page number
     */
    public void setPage(int page) {
        this._page = page;
    }
}
