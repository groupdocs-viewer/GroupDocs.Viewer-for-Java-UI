package com.groupdocs.viewerui.ui.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PageData {

	@JsonProperty("pageUrl")
	private String _pageUrl;

	@JsonProperty("number")
	private int _number;

	@JsonProperty("width")
	private int _width;

	@JsonProperty("height")
	private int _height;

	@JsonProperty("thumbUrl")
	private String _thumbUrl;

	public PageData(int number, int width, int height) {
		this(number, width, height, null);
	}

	public PageData(int number, int width, int height, String pageUrl) {
		this(number, width, height, pageUrl, null);
	}

	public PageData(int number, int width, int height, String pageUrl, String thumbUrl) {
		this._number = number;
		this._width = width;
		this._height = height;
		this._pageUrl = pageUrl;
		this._thumbUrl = thumbUrl;
	}

	/**
	 * Page with in pixels.
	 */
	public int getWidth() {
		return _width;
	}

	public void setWidth(int width) {
		this._width = width;
	}

	/**
	 * Page height in pixels.
	 */
	public int getHeight() {
		return _height;
	}

	public void setHeight(int height) {
		this._height = height;
	}


	public String getPageUrl() {
		return _pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this._pageUrl = pageUrl;
	}

	public int getNumber() {
		return _number;
	}

	public void setNumber(int number) {
		this._number = number;
	}

	public String getThumbUrl() {
		return _thumbUrl;
	}

	public void setThumbUrl(String thumbUrl) {
		this._thumbUrl = thumbUrl;
	}
}
