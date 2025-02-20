package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatePagesRequest {

	@JsonProperty("file")
	private String _file;

	@JsonProperty("fileType")
	private String _fileType;

	@JsonProperty("password")
	private String _password;

	@JsonProperty("pages")
	private int[] _pages;

	/**
	 * File unique ID.
	 */
	public String getFile() {
		return _file;
	}

	public void setFile(String file) {
		this._file = file;
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
	 * The pages to return.
	 */
	public int[] getPages() {
		return _pages;
	}

	public void setPages(int[] pages) {
		this._pages = pages;
	}

}
