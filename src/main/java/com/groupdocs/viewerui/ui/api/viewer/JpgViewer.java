package com.groupdocs.viewerui.ui.api.viewer;

import com.groupdocs.viewer.Viewer;
import com.groupdocs.viewer.options.JpgViewOptions;
import com.groupdocs.viewer.options.ViewInfoOptions;
import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.api.IFileTypeResolver;
import com.groupdocs.viewerui.ui.api.internalcaching.IInternalCache;
import com.groupdocs.viewerui.ui.api.licensing.IViewerLicenser;
import com.groupdocs.viewerui.ui.configuration.ViewerConfig;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.IPageFormatter;
import com.groupdocs.viewerui.ui.core.entities.FileCredentials;
import com.groupdocs.viewerui.ui.core.entities.JpgPage;
import com.groupdocs.viewerui.ui.core.entities.Page;
import com.groupdocs.viewerui.ui.core.extensions.CopyExtensions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class JpgViewer extends BaseViewer {

	private final ViewerConfig _viewerConfig;

	public JpgViewer(ViewerConfig viewerConfig, IViewerLicenser licenser, IInternalCache internalCache,
					 FileStorageProvider fileStorageProvider, IFileTypeResolver fileTypeResolver, IPageFormatter pageFormatter) {
		super(viewerConfig, licenser, internalCache, fileStorageProvider, fileTypeResolver, pageFormatter);
		_viewerConfig = viewerConfig;
	}

	@Override
	public String getPageExtension() {
		return JpgPage.EXTENSION;
	}

	@Override
	public Page createPage(int pageNumber, byte[] data) {
		return new JpgPage(pageNumber, data);
	}

	@Override
	public byte[] getPageResource(FileCredentials fileCredentials, int pageNumber, String resourceName) {
		throw new ViewerUiException("JpgViewer does not support retrieving external HTML resources.");
	}

	@Override
	protected Page renderPage(Viewer viewer, String filePath, int pageNumber) {
		try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			JpgViewOptions viewOptions = createViewOptions(byteArrayOutputStream);

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

	@Override
	protected ViewInfoOptions createViewInfoOptions() {
		return ViewInfoOptions.fromJpgViewOptions(_viewerConfig.getJpgViewOptions());
	}

	private JpgViewOptions createViewOptions(OutputStream pageStream) {
		JpgViewOptions viewOptions = new JpgViewOptions(i -> pageStream, (i, closeable) -> {

		});

		CopyExtensions.copyJpgViewOptions(_viewerConfig.getJpgViewOptions(), viewOptions);

		return viewOptions;
	}

}
