package com.groupdocs.viewerui.ui.core;

import com.groupdocs.viewerui.Keys;
import com.groupdocs.viewerui.exception.ViewerUiException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class UiEmbeddedResourcesReader implements IUiResourcesReader {

	private static final String BASIC_RESOURCE_PATH = "com/groupdocs/viewerui";

	@Override
	public UiResource getUiResource(String resourceName) throws IOException {
		final ClassLoader classLoader = getClass().getClassLoader();
		try (final InputStream resourceAsStream = classLoader
			.getResourceAsStream(BASIC_RESOURCE_PATH + "/" + resourceName)) {
			if (resourceAsStream == null) {
				throw new ViewerUiException(
						"Resource with name '" + Keys.GROUPDOCSVIEWERUI_MAIN_UI_RESOURCE + "' was not found");
			}
			FileNameMap fileNameMap = URLConnection.getFileNameMap();
			String mimeType = fileNameMap.getContentTypeFor(resourceName);

			final String resourceContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
			return UiResource.create(resourceName, resourceContent, mimeType);
		}
	}

}
