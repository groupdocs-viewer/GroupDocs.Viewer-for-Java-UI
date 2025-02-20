package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileDescription {

	@JsonProperty("path")
	private String _path;

	@JsonProperty("name")
	private String _name;

	@JsonProperty("isDir")
	private boolean _isDir;

	@JsonProperty("size")
	private long _size;

	/**
	 * .ctor
	 */
	public FileDescription(String path, String name, boolean isDirectory, long size) {
		_path = path;
		_name = name;
		_isDir = isDirectory;
		_size = size;
	}

	/**
	 * File unique ID.
	 */
	// [JsonPropertyName("path")]
	public String getPath() {
		return _path;
	}

	/**
	 * File file name.
	 */
	// [JsonPropertyName("name")]
	public String getName() {
		return _name;
	}

	/**
	 * <value>True</value> when it instanceof a directory.
	 */
	// [JsonPropertyName("isDirectory")]
	public boolean getIsDir() {
		return _isDir;
	}

	/**
	 * Size in bytes.
	 */
	// [JsonPropertyName("size")]
	public long getSize() {
		return _size;
	}

}
