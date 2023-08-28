package com.groupdocs.viewerui.ui.api.viewer;

import com.groupdocs.viewer.Viewer;
import com.groupdocs.viewer.options.HtmlViewOptions;
import com.groupdocs.viewer.options.ViewInfoOptions;
import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.api.FileTypeResolver;
import com.groupdocs.viewerui.ui.api.internalcaching.InternalCache;
import com.groupdocs.viewerui.ui.api.licensing.ViewerLicenser;
import com.groupdocs.viewerui.ui.configuration.ViewerConfig;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.PageFormatter;
import com.groupdocs.viewerui.ui.core.entities.FileCredentials;
import com.groupdocs.viewerui.ui.core.entities.HtmlPage;
import com.groupdocs.viewerui.ui.core.entities.Page;
import com.groupdocs.viewerui.ui.core.extensions.CopyExtensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HtmlWithEmbeddedResourcesViewer extends BaseViewer {
	private static final Logger LOGGER = LoggerFactory.getLogger(HtmlWithEmbeddedResourcesViewer.class);

	private ViewerConfig _viewerConfig;

	public HtmlWithEmbeddedResourcesViewer(ViewerConfig viewerConfig, ViewerLicenser licenser,
                                           InternalCache viewerCache, FileStorageProvider fileStorageProvider, FileTypeResolver fileTypeResolver,
                                           PageFormatter pageFormatter) {
		super(viewerConfig, licenser, viewerCache, fileStorageProvider, fileTypeResolver, pageFormatter);
		_viewerConfig = viewerConfig;
	}

	@Override
	public String getPageExtension() {
		return HtmlPage.EXTENSION;
	}

	@Override
	public Page createPage(int pageNumber, byte[] data) {
		return new HtmlPage(pageNumber, data);
	}

	@Override
	public byte[] getPageResource(FileCredentials fileCredentials, int pageNumber, String resourceName) {
		throw new ViewerUiException(
				"HtmlWithEmbeddedResourcesViewer does not support retrieving external HTML resources.");
	}

	@Override
	protected ViewInfoOptions createViewInfoOptions() {
		return ViewInfoOptions.fromHtmlViewOptions(_viewerConfig.getHtmlViewOptions());
	}

	@Override
	protected Page renderPage(Viewer viewer, String filePath, int pageNumber) {
		try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			HtmlViewOptions viewOptions = createViewOptions(byteArrayOutputStream);

			viewer.view(viewOptions, pageNumber);

			byte[] bytes = byteArrayOutputStream.toByteArray();
			Page page = createPage(pageNumber, bytes);

			return page;
		}
		catch (IOException e) {
			LOGGER.error("Exception throws while rendering html page with embedded resources: filePath={}, pageNumber={}", filePath, pageNumber, e);
			throw new ViewerUiException(e);
		}
	}

	private HtmlViewOptions createViewOptions(OutputStream pageStream) {
		HtmlViewOptions viewOptions = HtmlViewOptions.forEmbeddedResources(i -> pageStream, (i, closeable) -> {

		});

		CopyExtensions.copyHtmlViewOptions(_viewerConfig.getHtmlViewOptions(), viewOptions);

		return viewOptions;
	}

}
