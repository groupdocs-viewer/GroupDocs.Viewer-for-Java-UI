package com.groupdocs.viewerui.ui.api.models;

public class LoadDocumentPageRequest {

	private String _guid;

	private String _fileType;

	private String _password;

	private int _page;

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
	 * The page to return.
	 */
	// [JsonPropertyName("page")]
	public int getPage() {
		return _page;
	}

	public void setPage(int page) {
		this._page = page;
	}

}
