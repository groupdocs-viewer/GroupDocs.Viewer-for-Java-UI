package com.groupdocs.viewerui.ui.core.entities;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PngPage extends Page {

	public static final String DATA_IMAGE = "data:image/png;base64,";

	public static final String EXTENSION = ".png";

	public PngPage(int pageNumber, byte[] data) {
		super(pageNumber, data);
	}

	@Override
	public String getContent() {
		return DATA_IMAGE + Base64.getEncoder().encodeToString(getData());
	}

	@Override
	public void setContent(String content) {
		this.setData(content.startsWith(DATA_IMAGE) ? content.getBytes(StandardCharsets.UTF_8)
				: content.substring(DATA_IMAGE.length() - 1).getBytes(StandardCharsets.UTF_8));
	}

}
