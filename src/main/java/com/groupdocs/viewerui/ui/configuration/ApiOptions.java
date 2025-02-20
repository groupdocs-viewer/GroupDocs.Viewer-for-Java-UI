package com.groupdocs.viewerui.ui.configuration;

public class ApiOptions {

	private String _apiEndpoint = "/viewer-api";


	/**
	 * The API path or endpoint. The default value is "/viewer-api"
	 */
	public String getApiEndpoint() {
		return _apiEndpoint;
	}

	public void setApiEndpoint(String apiEndpoint) {
		this._apiEndpoint = apiEndpoint;
	}

	@Override
	public String toString() {
		return "ApiOptions {" +
				" apiEndpoint='" + _apiEndpoint + '\'' +
				" }";
	}
}
