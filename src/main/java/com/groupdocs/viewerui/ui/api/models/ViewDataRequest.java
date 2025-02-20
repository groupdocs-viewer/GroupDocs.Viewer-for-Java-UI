package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ViewDataRequest {

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

	public void setGuid(String file) {
		this._file = file;
	}

	/**
	 * File type e.g "docx".
	 */
	public String getFileType() {
		return _fileType;
	}

	public void setFileType(String FileType) {
		this._fileType = FileType;
	}

	/**
	 * The password to open a document.
	 */
	public String getPassword() {
		return _password;
	}

	public void setPassword(String Password) {
		this._password = Password;
	}
}
