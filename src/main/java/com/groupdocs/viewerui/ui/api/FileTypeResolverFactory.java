package com.groupdocs.viewerui.ui.api;

public class FileTypeResolverFactory {
    private static FileTypeResolver _fileTypeResolver;

    public static synchronized FileTypeResolver getInstance() {
        if (_fileTypeResolver == null) {
            _fileTypeResolver = new FileExtensionFileTypeResolver();
        }
        return _fileTypeResolver;
    }

    public static void setInstance(FileTypeResolver fileTypeResolver) {
        FileTypeResolverFactory._fileTypeResolver = fileTypeResolver;
    }
}
