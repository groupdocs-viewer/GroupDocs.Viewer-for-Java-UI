package com.groupdocs.viewerui.ui.core;

public class UiResource {

	private String _content;

	private String _contentType;

	private String _fileName;

	private UiResource(String fileName, String content, String contentType) {
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

	public static UiResource create(String fileName, String content, String contentType) {
		return new UiResource(fileName, content, contentType);
	}

	public String getContent() {
		return _content;
	}

	public void setContent(String Content) {
		_content = Content;
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
