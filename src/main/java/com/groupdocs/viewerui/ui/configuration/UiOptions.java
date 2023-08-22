package com.groupdocs.viewerui.ui.configuration;

import com.groupdocs.viewerui.exception.ViewerUiException;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class UiOptions {

	public String _uiPath = "/viewer";

	public String _uiConfigEndpoint = "/viewer-config";

	private Set<String> _customStylesheets = new HashSet<>();

	public UiOptions addCustomStylesheet(String path) {
		Path stylesheetPath = Paths.get(path);

		try {
			if (Files.notExists(stylesheetPath)) {
				stylesheetPath = Paths.get(getClass().getClassLoader().getResource(path).toURI());
			}
		}
		catch (URISyntaxException e) {
			// Log error
			e.printStackTrace();
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

	public String getUiConfigEndpoint() {
		return _uiConfigEndpoint;
	}

	public void setUiConfigEndpoint(String uiConfigEndpoint) {
		this._uiConfigEndpoint = uiConfigEndpoint;
	}

	public Set<String> getCustomStylesheets() {
		return _customStylesheets;
	}

	public void setCustomStylesheets(Set<String> customStylesheets) {
		this._customStylesheets = customStylesheets;
	}

}
