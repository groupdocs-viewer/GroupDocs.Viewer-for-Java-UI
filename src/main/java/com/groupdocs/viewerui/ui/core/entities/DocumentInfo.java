package com.groupdocs.viewerui.ui.core.entities;

import java.util.List;

public class DocumentInfo {

	private String _fileType;

	private boolean _printAllowed;

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
