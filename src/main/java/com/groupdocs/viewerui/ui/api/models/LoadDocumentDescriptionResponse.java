package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LoadDocumentDescriptionResponse {

	@JsonProperty("guid")
	private String _guid;

	@JsonProperty("fileType")
	private String _fileType;

	@JsonProperty("printAllowed")
	private boolean _printAllowed;

	@JsonProperty("pages")
	private List<PageDescription> _pages;

	@JsonProperty("searchTerm")
	private String _searchTerm;

	public LoadDocumentDescriptionResponse(String guid, String fileType, boolean printAllowed,
			List<PageDescription> pages, String searchTerm) {
		this._guid = guid;
		this._fileType = fileType;
		this._printAllowed = printAllowed;
		this._pages = pages;
		this._searchTerm = searchTerm;
	}

	/**
	 * File unique ID.
	 */
	// [JsonPropertyName("guid")]
	public String getGuid() {
		return _guid;
	}

	public void setGuid(String guid) {
		this._guid = guid;
	}

	/**
	 * File type e.g "docx".
	 */
	// [JsonPropertyName("fileType")]
	public String getFileType() {
		return _fileType;
	}

	public void setFileType(String fileType) {
		this._fileType = fileType;
	}

	/**
	 * Indicates if printing of the document instanceof allowed.
	 */
	// [JsonPropertyName("printAllowed")]
	public boolean isPrintAllowed() {
		return _printAllowed;
	}

	public void setPrintAllowed(boolean printAllowed) {
		this._printAllowed = printAllowed;
	}

	/**
	 * Document pages.
	 */
	// [JsonPropertyName("pages")]
	public List<PageDescription> getPages() {
		return _pages;
	}

	public void setPages(List<PageDescription> pages) {
		this._pages = pages;
	}

	/**
	 * Search term from back to UI search after load document.
	 */
	// [JsonPropertyName("searchTerm")]
	public String getSearchTerm() {
		return _searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this._searchTerm = searchTerm;
	}

}
