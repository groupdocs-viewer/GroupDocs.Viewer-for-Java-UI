package com.groupdocs.viewerui.ui.core.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DocumentInfo {

	@JsonProperty("fileType")
	private String _fileType;

	@JsonProperty("printAllowed")
	private boolean _printAllowed;

	@JsonProperty("pages")
	private List<PageInfo> _pages;

	public String getFileType() {
		return _fileType;
	}

	public void setFileType(String fileType) {
		this._fileType = fileType;
	}

	public boolean isPrintAllowed() {
		return _printAllowed;
	}

	public void setPrintAllowed(boolean printAllowed) {
		this._printAllowed = printAllowed;
	}

	public List<PageInfo> getPages() {
		return _pages;
	}

	public void setPages(List<PageInfo> pages) {
		this._pages = pages;
	}

}
