package com.groupdocs.viewerui.ui.core;

import com.groupdocs.viewerui.ui.configuration.UiOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UiStylesheet {

	private final static String STYLESHEETS_PATH = "css";

	private String _fileName;

	private byte[] _content;

	private String _resourcePath;

	private String _resourceRelativePath;

	private UiStylesheet(UiOptions uiOptions, String filePath) {
		final Path stylesheetPath = Paths.get(filePath);
		try {
			setFileName(stylesheetPath.getFileName().toString());
			setContent(Files.readAllBytes(stylesheetPath));
			setResourcePath(uiOptions.getUiPath() + "/" + STYLESHEETS_PATH + "/" + getFileName());
			setResourceRelativePath(STYLESHEETS_PATH + "/" + getFileName());
		}
		catch (IOException e) {
			e.printStackTrace(); // TODO: Add logging
			throw new RuntimeException(e);
		}
	}

	public static UiStylesheet create(UiOptions uiOptions, String filePath) {
		return new UiStylesheet(uiOptions, filePath);
	}

	public String getFileName() {
		return _fileName;
	}

	public void setFileName(String fileName) {
		this._fileName = fileName;
	}

	public byte[] getContent() {
		return _content;
	}

	public void setContent(byte[] content) {
		this._content = content;
	}

	public String getResourcePath() {
		return _resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this._resourcePath = resourcePath;
	}

	public String getResourceRelativePath() {
		return _resourceRelativePath;
	}

	public void setResourceRelativePath(String resourceRelativePath) {
		this._resourceRelativePath = resourceRelativePath;
	}

}
