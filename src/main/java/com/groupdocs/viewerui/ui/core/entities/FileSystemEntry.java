package com.groupdocs.viewerui.ui.core.entities;

public class FileSystemEntry {

	private String _fileName;

	private String _filePath;

	private boolean _isDirectory;

	private long _size;

	private FileSystemEntry(String fileName, String filePath, boolean isDirectory, long size) {
		this._fileName = fileName;
		this._filePath = filePath;
		this._isDirectory = isDirectory;
		this._size = size;
	}

	public static FileSystemEntry directory(String name, String path, long size) {
		return new FileSystemEntry(name, path, true, size);
	}

	public static FileSystemEntry file(String name, String path, long size) {
		return new FileSystemEntry(name, path, false, size);
	}

	public String getFileName() {
		return _fileName;
	}

	public void setFileName(String fileName) {
		_fileName = fileName;
	}

	public String getFilePath() {
		return _filePath;
	}

	public void setFilePath(String filePath) {
		_filePath = filePath;
	}

	public boolean isDirectory() {
		return _isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		_isDirectory = isDirectory;
	}

	public long getSize() {
		return _size;
	}

	public void setSize(long size) {
		_size = size;
	}

}
