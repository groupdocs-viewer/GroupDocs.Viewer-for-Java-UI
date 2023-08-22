package com.groupdocs.viewerui.ui.api.models;

public class PrintPdfRequest {

	private String _guid;

	private String _fileType;

	private String _password;

	/**
	 * Unique file ID.
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
	 * Password to open the document.
	 */
	// [JsonPropertyName("password")]
	public String getPassword() {
		return _password;
	}

	public void setPassword(String password) {
		this._password = password;
	}

}
