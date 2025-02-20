package com.groupdocs.viewerui.ui.api.configuration;

/**
 * Configuration options for API connection settings.
 */
public class Options {
    public String apiDomain;

    public String apiPath = "/viewer-api";

    /**
     * Specifies the base domain for the API, including the protocol and host.
     * If not explicitly set, the default value will be inferred from the current HTTP context.
     * Example: "https://localhost:5001" or "https://api.example.com".
     */
    public String getApiDomain() {
        return apiDomain;
    }

    public void setApiDomain(String apiDomain) {
        this.apiDomain = apiDomain;
    }

    /**
     * Specifies the path for the API endpoint relative to the domain.
     * Default: "/viewer-api".
     * Example: "/api/v1" or "/custom-path".
     */
    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }
}
