package com.groupdocs.viewerui.ui.api;

public class FileNameResolverFactory {
    private static FileNameResolver _fileNameResolver;

    public static synchronized FileNameResolver getInstance() {
        if (_fileNameResolver == null) {
            _fileNameResolver = new FilePathFileNameResolver();
        }
        return _fileNameResolver;
    }

    public static void setInstance(FileNameResolver fileNameResolver) {
        FileNameResolverFactory._fileNameResolver = fileNameResolver;
    }
}
