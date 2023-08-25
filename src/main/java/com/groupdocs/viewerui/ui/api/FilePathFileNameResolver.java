package com.groupdocs.viewerui.ui.api;

import java.nio.file.Paths;

public class FilePathFileNameResolver implements FileNameResolver {

    @Override
    public String resolveFileName(String filePath) {
        return Paths.get(filePath).getFileName().toString();
    }
}
