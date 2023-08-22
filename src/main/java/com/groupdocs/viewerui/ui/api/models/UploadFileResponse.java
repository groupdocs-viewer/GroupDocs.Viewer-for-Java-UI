package com.groupdocs.viewerui.ui.api.models;

public class UploadFileResponse {

	private final String _guid;

	/**
	 * .ctor
	 */
	public UploadFileResponse(String filePath) {
		_guid = filePath;
	}

	/**
	 * Unique file ID.
	 */
	// [JsonPropertyName("guid")]
	public String getGuid() {
		return _guid;
	}

}
