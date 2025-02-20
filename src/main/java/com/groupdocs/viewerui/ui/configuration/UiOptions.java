package com.groupdocs.viewerui.ui.configuration;

import com.groupdocs.viewerui.exception.ViewerUiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class UiOptions {
	private static final Logger LOGGER = LoggerFactory.getLogger(UiOptions.class);

	private String _uiPath = "/viewer";

	private String _uiTitle = "GroupDocs.Viewer UI";

	private Set<String> _customStylesheets = new HashSet<>();

	public UiOptions addCustomStylesheet(String path) {
		Path stylesheetPath = Paths.get(path);

		try {
			if (Files.notExists(stylesheetPath)) {
				stylesheetPath = Paths.get(getClass().getClassLoader().getResource(path).toURI());
			}
		}
		catch (URISyntaxException e) {
			LOGGER.error("Exception throws while parsing resource uri: path={}", path, e);
			throw new ViewerUiException(e);
		}

		if (!Files.notExists(stylesheetPath)) {
			throw new ViewerUiException("Could not find style sheet at path '" + stylesheetPath + "'");
		}

		_customStylesheets.add(stylesheetPath.toString());

		return this;
	}

	public String getUiPath() {
		return _uiPath;
	}

	public void setUiPath(String uiPath) {
		this._uiPath = uiPath;
	}

	public Set<String> getCustomStylesheets() {
		return _customStylesheets;
	}

	public void setCustomStylesheets(Set<String> customStylesheets) {
		this._customStylesheets = customStylesheets;
	}

	/**
	 * HTML document title. The default value is "GroupDocs.Viewer UI".
	 */
	public String getUiTitle() {
		return _uiTitle;
	}

	public void setUiTitle(String uiTitle) {
		this._uiTitle = uiTitle;
	}

	@Override
	public String toString() {
		return "UiOptions {" +
				" uiPath='" + _uiPath + '\'' +
				", uiTitle='" + _uiTitle + '\'' +
				", customStylesheets=" + _customStylesheets +
				" }";
	}
}
