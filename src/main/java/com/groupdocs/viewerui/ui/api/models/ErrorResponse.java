package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {


	@JsonProperty("message")
	private String _message;

	public ErrorResponse(String message) {
		this.setMessage(message);
	}

	/**
	 * The error message.
	 */
	// [JsonPropertyName("message")]
	public String getMessage() {
		return _message;
	}

	public void setMessage(String message) {
		this._message = message;
	}

}
