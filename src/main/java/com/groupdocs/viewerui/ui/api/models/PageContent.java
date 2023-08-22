package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PageContent {

	@JsonProperty("number")
	private int _number;

	@JsonProperty("data")
	private String _data;

	public PageContent(int number, String data) {
		this._number = number;
		this._data = data;
	}

	/**
	 * Page number.
	 */
	// [JsonPropertyName("number")]
	public int getNumber() {
		return _number;
	}

	public void setNumber(int number) {
		this._number = number;
	}

	/**
	 * Page contents. It can be HTML or base64-encoded image.
	 */
	// [JsonPropertyName("data")]
	public String getData() {
		return _data;
	}

	public void setData(String data) {
		this._data = data;
	}

}
