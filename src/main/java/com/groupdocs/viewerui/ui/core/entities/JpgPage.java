package com.groupdocs.viewerui.ui.core.entities;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JpgPage extends Page {

	public static final String DEFAULT_EXTENSION = ".jpeg";

	public static final String DATA_IMAGE = "data:image/jpeg;base64,";

	public JpgPage(int pageNumber, byte[] data) {
		super(pageNumber, data);
	}

	@Override
	public String getContent() {
		return DATA_IMAGE + Base64.getEncoder().encodeToString(getPageData());
	}

	@Override
	public void setContent(String content) {
		this.setData(content.startsWith(DATA_IMAGE) ? content.getBytes(StandardCharsets.UTF_8)
				: content.substring(DATA_IMAGE.length() - 1).getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String getContentType() {
		return "image/jpeg";
	}

}
