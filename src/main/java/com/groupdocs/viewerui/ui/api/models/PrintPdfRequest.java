package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrintPdfRequest {

	@JsonProperty("guid")
	private String _guid;

	@JsonProperty("fileType")
	private String _fileType;

	@JsonProperty("password")
	private String _password;

	/**
	 * Unique file ID.
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
	 * Password to open the document.
	 */
	public String getPassword() {
		return _password;
	}

	public void setPassword(String password) {
		this._password = password;
	}

}
