package com.groupdocs.viewerui.ui.api.utils;

public interface IApiUrlBuilder {

    String buildPageUrl(String file, int page, String extension);

    String buildThumbUrl(String file, int page, String extension);

    String buildPdfUrl(String file);

    String buildResourceUrl(String file, int page, String resource);

    String buildResourceUrl(String file, String pageTemplate, String resourceTemplate);
}
