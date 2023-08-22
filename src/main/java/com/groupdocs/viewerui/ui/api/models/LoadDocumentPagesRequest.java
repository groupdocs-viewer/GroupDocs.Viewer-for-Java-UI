package com.groupdocs.viewerui.ui.api.models;

public class LoadDocumentPagesRequest {

	private String _guid;

	private String _fileType;

	private String _password;

	private int[] _pages;

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
	 * File type e.g. "docx".
	 */
	// [JsonPropertyName("fileType")]
	public String getFileType() {
		return _fileType;
	}

	public void setFileType(String fileType) {
		this._fileType = fileType;
	}

	/**
	 * The password to open a document.
	 */
	// [JsonPropertyName("password")]
	public String getPassword() {
		return _password;
	}

	public void setPassword(String password) {
		this._password = password;
	}

	/**
	 * The pages to return.
	 */
	// [JsonPropertyName("pages")]
	public int[] getPages() {
		return _pages;
	}

	public void setPages(int[] pages) {
		this._pages = pages;
	}

}
