package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoadFileTreeRequest {

	@JsonProperty("path")
	private String _path = "";

	/**
	 * Folder path.
	 */
	// [JsonPropertyName("path")]
	public String getPath() {
		return _path;
	}

	public void setPath(String Path) {
		this._path = Path;
	}

}
