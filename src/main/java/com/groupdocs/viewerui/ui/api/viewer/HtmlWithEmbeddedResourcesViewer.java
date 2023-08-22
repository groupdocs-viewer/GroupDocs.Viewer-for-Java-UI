package com.groupdocs.viewerui.ui.api.viewer;

import com.groupdocs.viewer.Viewer;
import com.groupdocs.viewer.options.HtmlViewOptions;
import com.groupdocs.viewer.options.ViewInfoOptions;
import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.api.IFileTypeResolver;
import com.groupdocs.viewerui.ui.api.internalcaching.IInternalCache;
import com.groupdocs.viewerui.ui.api.licensing.IViewerLicenser;
import com.groupdocs.viewerui.ui.configuration.ViewerConfig;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.IPageFormatter;
import com.groupdocs.viewerui.ui.core.entities.FileCredentials;
import com.groupdocs.viewerui.ui.core.entities.HtmlPage;
import com.groupdocs.viewerui.ui.core.entities.Page;
import com.groupdocs.viewerui.ui.core.extensions.CopyExtensions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HtmlWithEmbeddedResourcesViewer extends BaseViewer {

	private ViewerConfig _viewerConfig;

	public HtmlWithEmbeddedResourcesViewer(ViewerConfig viewerConfig, IViewerLicenser licenser,
										   IInternalCache viewerCache, FileStorageProvider fileStorageProvider, IFileTypeResolver fileTypeResolver,
										   IPageFormatter pageFormatter) {
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
			e.printStackTrace(); // TODO: Add logging
			throw new RuntimeException(e);
		}
	}

	private HtmlViewOptions createViewOptions(OutputStream pageStream) {
		HtmlViewOptions viewOptions = HtmlViewOptions.forEmbeddedResources(i -> pageStream, (i, closeable) -> {

		});

		CopyExtensions.copyHtmlViewOptions(_viewerConfig.getHtmlViewOptions(), viewOptions);

		return viewOptions;
	}

}
