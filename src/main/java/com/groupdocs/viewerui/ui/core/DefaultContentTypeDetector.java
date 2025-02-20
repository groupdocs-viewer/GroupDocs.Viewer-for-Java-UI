package com.groupdocs.viewerui.ui.core;

import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultContentTypeDetector implements IContentTypeDetector {
    /**
     * Detects the content type of the given resource name.
     *
     * @param resourceName The name of the resource for which to detect the content type.
     * @return The content type of the resource, or "application/octet-stream" if it cannot be determined.
     */
    @Override
    public String detect(String resourceName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentType = fileNameMap.getContentTypeFor(resourceName);
        if (contentType == null) {
            Path path = Paths.get(resourceName);
            try {
                contentType = Files.probeContentType(path);
            } catch (IOException e) {
                return "application/octet-stream";
            }
        }

        return contentType;
    }
}
