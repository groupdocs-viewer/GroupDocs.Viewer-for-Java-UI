package com.groupdocs.viewerui.ui.api;

import com.groupdocs.viewer.FileType;

public interface IFileTypeResolver {

	FileType resolveFileType(String filePath);

}
