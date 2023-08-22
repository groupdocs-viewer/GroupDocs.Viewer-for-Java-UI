package com.groupdocs.viewerui.ui.core.extensions;

public class StringExtensions {

	public static String contentTypeFromFileName(String filename) {
		String extension = filename.substring(filename.lastIndexOf('.'));

		switch (extension) {
			case ".css":
				return "text/css";
			case ".woff":
				return "font/woff";
			case ".png":
				return "image/png";
			case ".jpg":
			case ".jpeg":
				return "image/jpeg";
			case ".svg":
				return "image/svg+xml";
			default:
				return "application/octet-stream";
		}
	}

	public static String changeExtension(String filePath, String newExtension) {
		return filePath.substring(0, filePath.lastIndexOf('.')) + newExtension;
	}

	public static String withTrailingSlash(String resourcePath) {
		return resourcePath.endsWith("/") ? resourcePath : resourcePath + "/";
	}

	public static String trimTrailingSlash(String resourcePath) {
		return resourcePath.replaceAll("/$", "");
	}

	public static String asRelativeResource(String resourcePath) {
		return resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
	}

}
