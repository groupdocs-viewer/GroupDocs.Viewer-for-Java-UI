package com.groupdocs.viewerui.ui.api.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.groupdocs.viewerui.Keys;

import java.net.HttpURLConnection;

public class ViewerActionResult {

	@JsonProperty("contentType")
	private String _contentType;

	@JsonProperty("contentLength")
	private long _contentLength;

	@JsonProperty("statusCode")
	private int _statusCode;

	@JsonProperty("value")
	private Object _value;

	public ViewerActionResult(Object value) {
		this(Keys.DEFAULT_RESPONSE_CONTENT_TYPE, HttpURLConnection.HTTP_OK, value);
	}

	public ViewerActionResult(String contentType, int statusCode, Object value) {
		this(contentType, -1, statusCode, value);
	}

	public ViewerActionResult(String contentType, long contentLength, int statusCode, Object value) {
		this._contentType = contentType;
		this._contentLength = contentLength;
		this._statusCode = statusCode;
		this._value = value;
	}

	public String getContentType() {
		return _contentType;
	}

	public void setContentType(String contentType) {
		this._contentType = contentType;
	}

	public long getContentLength() {
		return _contentLength;
	}

	public void setContentLength(long contentLength) {
		this._contentLength = contentLength;
	}

	public int getStatusCode() {
		return _statusCode;
	}

	public void setStatusCode(int statusCode) {
		this._statusCode = statusCode;
	}

	public Object getValue() {
		return _value;
	}

	public void setValue(Object value) {
		this._value = value;
	}

}
