package com.groupdocs.viewerui.ui.core.entities;

public class FileCredentials {

	private String _filePath;

	private String _fileType;

	private String _password;

	public FileCredentials(String _filePath) {
		this._filePath = _filePath;
	}

	public FileCredentials(String filePath, String fileType, String password) {
		_filePath = filePath;
		_fileType = fileType;
		_password = password;
	}

	public String getFilePath() {
		return _filePath;
	}

	public String getFileType() {
		return _fileType;
	}

	public String getPassword() {
		return _password == null ? "" : _password;
	}

}
