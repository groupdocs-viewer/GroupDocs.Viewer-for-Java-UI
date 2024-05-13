package com.groupdocs.viewerui.ui.api;

import com.groupdocs.viewer.FileType;

import java.util.Locale;

public class FileExtensionFileTypeResolver implements FileTypeResolver {

	@Override
	public FileType resolveFileType(String filePath) {
		String extension = filePath.substring(filePath.lastIndexOf('.'));

		return FileType.fromExtension(extension.toLowerCase(Locale.ROOT));
	}

}
