package com.groupdocs.viewerui.ui.api.utils;

import com.aspose.zip.exceptions.ArgumentNullException;
import com.groupdocs.viewerui.ui.api.ApiNames;
import com.groupdocs.viewerui.ui.api.configuration.Options;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ApiUrlBuilder implements IApiUrlBuilder {
    private final Options _options;

    public ApiUrlBuilder(Options options) {
        _options = options;
    }

    /**
     * builds a relative URL using the API method name and query parameters.
     * Example:
     * <pre>
     * String url = UrlHelper.BuildUrl("viewer-api/get-page", new QueryStringParam("my-file.docx", 5));
     * System.out.println(url); // Output: /viewer-api/get-page?file=my-file.docx&page=5
     * </pre>
     *
     * @param apiMethodName The API method name, e.g., "viewer-api/get-page".
     * @param values        An object containing query parameter key-value pairs, e.g., field "file", value "my-file.docx".
     * @return The relative URL as a string, e.g., "/viewer-api/get-page?file=my-file.docx&page=5".
     */
    private static String buildUrl(String apiMethodName, QueryStringParam... values) {
        if (apiMethodName == null || apiMethodName.trim().isEmpty()) {
            throw new ArgumentNullException("API method name cannot be null or empty.");
        }

        final String baseUrl = "/" + (apiMethodName.startsWith("/") ? apiMethodName.substring(1) : apiMethodName);
        String queryString = buildQueryString(values);

        return queryString == null || queryString.trim().isEmpty() ? baseUrl : baseUrl + "?" + queryString;
    }

    /**
     * Builds a full URL using the API domain, path, method name, and query parameters.
     * <pre>
     * String url = UrlHelper.buildUrl("https://www.example.com", "viewer-api", "get-page", new QueryStringParam("file", "my-file.docx"), new QueryStringParam("page", 5));
     * System.out.println(url); // Output: https://www.example.com/viewer-api/get-page?file=my-file.docx&page=5
     * </pre>
     *
     * @param apiDomain     The base API domain, e.g., "https://www.example.com".
     * @param apiPath       The API path, e.g., "viewer-api".
     * @param apiMethodName The API method name, e.g., "get-page".
     * @param values        An object containing query parameter key-value pairs, e.g., new QueryStringParam("file", "my-file.docx"), new QueryStringParam("page", 5).
     * @return The full URL as a string, e.g., "https://www.example.com/viewer-api/get-page?file=my-file.docx&page=5".
     */
    private static String buildUrl(String apiDomain, String apiPath, String apiMethodName, QueryStringParam... values) {
        if (apiDomain == null || apiDomain.trim().isEmpty()) {
            throw new IllegalArgumentException("API domain cannot be null or empty.");
        }

        if (apiPath == null || apiPath.trim().isEmpty()) {
            throw new IllegalArgumentException("API path cannot be null or empty.");
        }

        if (apiMethodName == null || apiMethodName.trim().isEmpty()) {
            throw new IllegalArgumentException("API method name cannot be null or empty.");
        }

        // Ensure proper URL formatting
        String basePath = String.format("%s/%s/%s", trimEnd(apiDomain, "/"), trimStart(trimEnd(apiPath, "/"), "/"), trimStart(apiMethodName, "/"));
        String queryString = buildQueryString(values);

        return queryString == null || queryString.trim().isEmpty() ? basePath : basePath + "?" + queryString;
    }

    private static String buildQueryString(QueryStringParam... values) {
        if (values == null) {
            return "";
        }

        List<String> queryParameters = new ArrayList<>();
        for (QueryStringParam queryStringParam : values) {
            String value = queryStringParam.toString();

            if (value != null) {
                queryParameters.add(value);
            }
        }

        return String.join("&", queryParameters);
    }

    private static String trimStart(String str, String prefix) {
        if (str == null) {
            return null;
        }
        if (str.startsWith(prefix)) {
            return str.substring(prefix.length());
        }
        return str;
    }

    private static String trimEnd(String str, String suffix) {
        if (str == null) {
            return null;
        }
        if (str.endsWith(suffix)) {
            return str.substring(0, str.length() - suffix.length());
        }
        return str;
    }

    public String getApiDomainOrDefault() {

        final String apiDomain = _options.getApiDomain();
        return apiDomain == null || apiDomain.isEmpty() ? "/" : _options.getApiDomain();
    }

    public String buildPageUrl(String file, int page, String extension) {
        return buildUrl(getApiDomainOrDefault(), _options.getApiPath(), ApiNames.API_METHOD_GET_PAGE,
                new QueryStringParam("file", file),
                new QueryStringParam("page", page));
    }

    public String buildThumbUrl(String file, int page, String extension) {
        return buildUrl(
                getApiDomainOrDefault(),
                _options.getApiPath(),
                ApiNames.API_METHOD_GET_THUMB,
                new QueryStringParam("file", file),
                new QueryStringParam("page", page));
    }

    public String buildPdfUrl(String file) {
        return buildUrl(
                getApiDomainOrDefault(),
                _options.getApiPath(),
                ApiNames.API_METHOD_GET_PDF,
                new QueryStringParam("file", file));
    }

    public String buildResourceUrl(String file, int page, String resource) {
        return buildUrl(
                getApiDomainOrDefault(),
                _options.getApiPath(),
                ApiNames.API_METHOD_GET_RESOURCE,
                new QueryStringParam("file", file),
                new QueryStringParam("page", page),
                new QueryStringParam("resource", resource));
    }

    public String buildResourceUrl(String file, String pageTemplate, String resourceTemplate) {
        return buildUrl(
                getApiDomainOrDefault(),
                _options.getApiPath(),
                ApiNames.API_METHOD_GET_RESOURCE,
                new QueryStringParam("file", file),
                new QueryStringParam("page", pageTemplate),
                new QueryStringParam("resource", resourceTemplate));
    }

    private static class QueryStringParam {
        private final String _paramName;
        private final Object _paramValue;

        public QueryStringParam(String paramName, Object paramValue) {
            this._paramName = paramName;
            this._paramValue = paramValue;
        }

        @Override
        public String toString() {
            try {
                return java.lang.String.format("%s=%s", URLEncoder.encode(this._paramName, StandardCharsets.UTF_8.toString()), URLEncoder.encode(java.lang.String.valueOf(this._paramValue), StandardCharsets.UTF_8.toString()));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
