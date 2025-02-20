package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatePdfRequest {

	@JsonProperty("file")
	private String _file;

	@JsonProperty("fileType")
	private String _fileType;

	@JsonProperty("password")
	private String _password;

	/**
	 * File unique ID.
	 */
	public String getFile() {
		return _file;
	}

	public void setFile(String guid) {
		this._file = guid;
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
