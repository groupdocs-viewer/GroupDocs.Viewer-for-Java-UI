package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ViewDataResponse {

	@JsonProperty("file")
	private String _file;

	@JsonProperty("fileName")
	private String _fileName;

	@JsonProperty("fileType")
	private String _fileType;

	@JsonProperty("canPrint")
	private boolean _canPrint;

	@JsonProperty("pages")
	private List<PageData> _pages;

	@JsonProperty("searchTerm")
	private String _searchTerm;

	public ViewDataResponse(String file, String fileType, String fileName, boolean canPrint, String searchTerm,
                            List<PageData> pages) {
		this._file = file;
		this._fileType = fileType;
		this._fileName = fileName;
		this._canPrint = canPrint;
		this._searchTerm = searchTerm;
		this._pages = pages;
	}

	/**
	 * File unique ID.
	 */
	// [JsonPropertyName("file")]
	public String getFile() {
		return _file;
	}

	public void setFile(String guid) {
		this._file = guid;
	}

	// [JsonPropertyName("fileName")]
	public String getFileName() {
		return _fileName;
	}

	public void setFileName(String fileName) {
		this._fileName = fileName;
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
	// [JsonPropertyName("canPrint")]
	public boolean isCanPrint() {
		return _canPrint;
	}

	public void setCanPrint(boolean canPrint) {
		this._canPrint = canPrint;
	}

	/**
	 * Document pages.
	 */
	// [JsonPropertyName("pages")]
	public List<PageData> getPages() {
		return _pages;
	}

	public void setPages(List<PageData> pages) {
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
