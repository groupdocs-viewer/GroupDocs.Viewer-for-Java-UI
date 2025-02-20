package com.groupdocs.viewerui.ui.core.entities;

import java.nio.charset.StandardCharsets;

public class HtmlPage extends Page {

	public static final String DEFAULT_EXTENSION = ".html";

	public HtmlPage(int pageNumber, byte[] data) {
		super(pageNumber, data);
	}

	@Override
	public String getContent() {
		return new String(getPageData(), StandardCharsets.UTF_8);
	}

	@Override
	public void setContent(String contents) {
		setData(contents.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String getContentType() {
		return "text/html";
	}

}
