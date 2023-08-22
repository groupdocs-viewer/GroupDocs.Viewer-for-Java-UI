package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileDescription {

	@JsonProperty("guid")
	private String _guid;

	@JsonProperty("name")
	private String _name;

	@JsonProperty("isDirectory")
	private boolean _isDirectory;

	@JsonProperty("size")
	private long _size;

	/**
	 * .ctor
	 */
	public FileDescription(String guid, String name, boolean isDirectory, long size) {
		_guid = guid;
		_name = name;
		_isDirectory = isDirectory;
		_size = size;
	}

	/**
	 * File unique ID.
	 */
	// [JsonPropertyName("guid")]
	public String getGuid() {
		return _guid;
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
	public boolean getIsDirectory() {
		return _isDirectory;
	}

	/**
	 * Size in bytes.
	 */
	// [JsonPropertyName("size")]
	public long getSize() {
		return _size;
	}

}
