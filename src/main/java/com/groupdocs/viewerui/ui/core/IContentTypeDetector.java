package com.groupdocs.viewerui.ui.core;

public interface IContentTypeDetector {
    /**
     * Detects the content type of the given resource name.
     *
     * @param resourceName The name of the resource for which to detect the content type.
     * @return The content type of the resource, or "application/octet-stream" if it cannot be determined.
     */
    String detect(String resourceName);
}
