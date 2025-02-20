package com.groupdocs.viewerui.ui.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultContentTypeDetector implements IContentTypeDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultContentTypeDetector.class);

    /**
     * Detects the content type of the given resource name.
     *
     * @param resourceName The name of the resource for which to detect the content type.
     * @return The content type of the resource, or "application/octet-stream" if it cannot be determined.
     */
    @Override
    public String detect(String resourceName) {
        String contentType = URLConnection.guessContentTypeFromName(resourceName);
        if (contentType == null) {
            if (resourceName.endsWith(".woff") || resourceName.endsWith(".woff2")) {
                contentType = "application/x-font-woff";
            } else {
                try {
                    Path path = Paths.get(resourceName);
                    contentType = Files.probeContentType(path);
                } catch (Exception e2) {
                    LOGGER.trace("Content type for '{}' was not detected, using 'application/octet-stream'", resourceName);
                }
            }
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return contentType;
    }
}
