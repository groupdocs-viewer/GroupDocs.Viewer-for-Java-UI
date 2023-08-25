package com.groupdocs.viewerui.ui.api.viewer;

import com.groupdocs.viewer.Viewer;
import com.groupdocs.viewer.options.PngViewOptions;
import com.groupdocs.viewer.options.ViewInfoOptions;
import com.groupdocs.viewerui.ui.api.FileTypeResolver;
import com.groupdocs.viewerui.ui.api.internalcaching.InternalCache;
import com.groupdocs.viewerui.ui.api.licensing.ViewerLicenser;
import com.groupdocs.viewerui.ui.configuration.ViewerConfig;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.PageFormatter;
import com.groupdocs.viewerui.ui.core.entities.FileCredentials;
import com.groupdocs.viewerui.ui.core.entities.Page;
import com.groupdocs.viewerui.ui.core.entities.PngPage;
import com.groupdocs.viewerui.ui.core.extensions.CopyExtensions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PngViewer extends BaseViewer {

	private final ViewerConfig _viewerConfig;

	public PngViewer(ViewerConfig viewerConfig, ViewerLicenser licenser, InternalCache internalCache,
                     FileStorageProvider fileStorageProvider, FileTypeResolver fileTypeResolver, PageFormatter pageFormatter) {
		super(viewerConfig, licenser, internalCache, fileStorageProvider, fileTypeResolver, pageFormatter);
		_viewerConfig = viewerConfig;
	}

	@Override
	public String getPageExtension() {
		return PngPage.EXTENSION;
	}

	@Override
	public Page createPage(int pageNumber, byte[] data) {
		return new PngPage(pageNumber, data);
	}

	@Override
	public byte[] getPageResource(FileCredentials fileCredentials, int pageNumber, String resourceName) {
		throw new RuntimeException("PngViewer does not support retrieving external HTML resources.");
	}

	@Override
	protected Page renderPage(Viewer viewer, String filePath, int pageNumber) {
		try (ByteArrayOutputStream pageStream = new ByteArrayOutputStream()) {
			PngViewOptions viewOptions = createViewOptions(pageStream);

			viewer.view(viewOptions, pageNumber);

			byte[] bytes = pageStream.toByteArray();
			Page page = createPage(pageNumber, bytes);

			return page;
		}
		catch (IOException e) {
			e.printStackTrace(); // TODO: Add logging
			throw new RuntimeException(e);
		}
	}

	@Override
	protected ViewInfoOptions createViewInfoOptions() {
		return ViewInfoOptions.fromJpgViewOptions(_viewerConfig.getJpgViewOptions());
	}

	private PngViewOptions createViewOptions(OutputStream pageStream) {
		PngViewOptions viewOptions = new PngViewOptions(i -> pageStream, (i, closeable) -> {
		});

		CopyExtensions.copyPngViewOptions(_viewerConfig.getPngViewOptions(), viewOptions);

		return viewOptions;
	}

}
