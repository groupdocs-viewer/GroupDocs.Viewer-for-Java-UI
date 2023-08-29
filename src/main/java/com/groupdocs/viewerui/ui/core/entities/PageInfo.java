package com.groupdocs.viewerui.ui.core.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PageInfo {

	@JsonProperty("width")
	private int _width;

	@JsonProperty("height")
	private int _height;

	@JsonProperty("number")
	private int _number;

	@JsonProperty("name")
	private String _name;

	public int getWidth() {
		return _width;
	}

	public void setWidth(int width) {
		this._width = width;
	}

	public int getHeight() {
		return _height;
	}

	public void setHeight(int height) {
		this._height = height;
	}

	public int getNumber() {
		return _number;
	}

	public void setNumber(int number) {
		this._number = number;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

}
