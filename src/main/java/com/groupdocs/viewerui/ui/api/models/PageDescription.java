package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PageDescription extends PageContent {

	@JsonProperty("width")
	private int _width;

	@JsonProperty("height")
	private int _height;

	@JsonProperty("sheetName")
	private String _sheetName;

	public PageDescription(int width, int height, String sheetName, int number, String data) {
		super(number, data);
		this._width = width;
		this._height = height;
		this._sheetName = sheetName;
	}

	/**
	 * Page with in pixels.
	 */
	// [JsonPropertyName("width")]
	public int getWidth() {
		return _width;
	}

	public void setWidth(int width) {
		this._width = width;
	}

	/**
	 * Page height in pixels.
	 */
	// [JsonPropertyName("height")]
	public int getHeight() {
		return _height;
	}

	public void setHeight(int height) {
		this._height = height;
	}

	/**
	 * Worksheet name for spreadsheets.
	 */
	// [JsonPropertyName("sheetName")]
	public String getSheetName() {
		return _sheetName;
	}

	public void setSheetName(String sheetName) {
		this._sheetName = sheetName;
	}

}
