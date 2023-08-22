package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoadDocumentDescriptionRequest {

	@JsonProperty("guid")
	private String _guid;

	@JsonProperty("fileType")
	private String _fileType;

	@JsonProperty("password")
	private String _password;

	/**
	 * File unique ID.
	 */
	public String getGuid() {
		return _guid;
	}

	public void setGuid(String Guid) {
		this._guid = Guid;
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
