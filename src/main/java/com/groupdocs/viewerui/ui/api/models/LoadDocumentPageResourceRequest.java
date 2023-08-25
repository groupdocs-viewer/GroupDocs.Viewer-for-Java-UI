package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoadDocumentPageResourceRequest {

	@JsonProperty("guid")
	private String _guid;

	@JsonProperty("fileType")
	private String _fileType;

	@JsonProperty("password")
	private String _password;

	@JsonProperty("pageNumber")
	private int _pageNumber;

	@JsonProperty("resourceName")
	private String _resourceName;

	/**
	 * File unique ID.
	 */
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
