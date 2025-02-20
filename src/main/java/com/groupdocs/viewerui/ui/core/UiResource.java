package com.groupdocs.viewerui.ui.core;

import java.nio.charset.StandardCharsets;

public class UiResource {

	private byte[] _content;

	private String _contentType;

	private String _fileName;

	private UiResource(String fileName, byte[] content, String contentType) {
		if (content == null) {
			throw new IllegalArgumentException("content");
		}
		setContent(content);
		if (contentType == null) {
			throw new IllegalArgumentException("contentType");
		}
		setContentType(contentType);
		if (fileName == null) {
			throw new IllegalArgumentException("fileName");
		}
		setFileName(fileName);
	}

	public static UiResource create(String fileName, byte[] content, String contentType) {
		return new UiResource(fileName, content, contentType);
	}

	public byte[] getContent() {
		return _content;
	}

	public String getContentAsString() {
		return new String(_content, StandardCharsets.UTF_8);
	}

	public void setContent(byte[] content) {
		_content = content;
	}

	public String getContentType() {
		return _contentType;
	}

	public void setContentType(String contentType) {
		this._contentType = contentType;
	}

	public String getFileName() {
		return _fileName;
	}

	public void setFileName(String fileName) {
		this._fileName = fileName;
	}

}
