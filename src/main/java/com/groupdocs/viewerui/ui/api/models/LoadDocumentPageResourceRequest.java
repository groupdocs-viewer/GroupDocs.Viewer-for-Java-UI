package com.groupdocs.viewerui.ui.api.models;

public class LoadDocumentPageResourceRequest {

	private String _guid;

	private String _fileType;

	private String _password;

	private int _pageNumber;

	private String _resourceName;

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
	public String getFileType() {
		return _fileType;
	}

	public void setFileType(String fileType) {
		this._fileType = fileType;
	}

	/**
	 * The password to open a document.
	 */
	public String getPassword() {
		return _password;
	}

	public void setPassword(String password) {
		this._password = password;
	}

	/**
	 * The page number which the resource belongs to.
	 */
	public int getPageNumber() {
		return _pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this._pageNumber = pageNumber;
	}

	/**
	 * The resource name e.g. "s.css".
	 */
	public String getResourceName() {
		return _resourceName;
	}

	public void setResourceName(String resourceName) {
		this._resourceName = resourceName;
	}

}
