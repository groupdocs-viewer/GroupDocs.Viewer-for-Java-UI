package com.groupdocs.viewerui.ui.api.viewer;

import com.groupdocs.viewer.Viewer;
import com.groupdocs.viewer.options.PngViewOptions;
import com.groupdocs.viewer.options.ViewInfoOptions;
import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.api.FileTypeResolver;
import com.groupdocs.viewerui.ui.api.configuration.ThumbSettings;
import com.groupdocs.viewerui.ui.api.internalcaching.InternalCache;
import com.groupdocs.viewerui.ui.api.licensing.ViewerLicenser;
import com.groupdocs.viewerui.ui.configuration.ViewerConfig;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.PageFormatter;
import com.groupdocs.viewerui.ui.core.entities.*;
import com.groupdocs.viewerui.ui.core.extensions.CopyExtensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PngViewer extends BaseViewer {
	private static final Logger LOGGER = LoggerFactory.getLogger(PngViewer.class);

	private final ViewerConfig _viewerConfig;

	public PngViewer(ViewerConfig viewerConfig, ViewerLicenser licenser, InternalCache internalCache,
					 FileStorageProvider fileStorageProvider, FileTypeResolver fileTypeResolver, PageFormatter pageFormatter) {
		super(viewerConfig, licenser, internalCache, fileStorageProvider, fileTypeResolver, pageFormatter);
		_viewerConfig = viewerConfig;
	}

	@Override
	public String getPageExtension() {
		return PngPage.DEFAULT_EXTENSION;
	}

	@Override
	public String getThumbExtension() {
		return PngThumb.DEFAULT_EXTENSION;
	}

	@Override
	public Page createPage(int pageNumber, byte[] data) {
		return new PngPage(pageNumber, data);
	}

	@Override
	public Thumb createThumb(int pageNumber, byte[] data) {
		return new PngThumb(pageNumber, data);
	}

	@Override
	public byte[] getPageResource(FileCredentials fileCredentials, int pageNumber, String resourceName) {
		throw new RuntimeException("PngViewer does not support retrieving external HTML resources.");
	}

	@Override
	protected Page renderPage(Viewer viewer, String filePath, int pageNumber) {
		try (ByteArrayOutputStream pageStream = new ByteArrayOutputStream()) {
			PngViewOptions viewOptions = createPageViewOptions(pageStream);

			viewer.view(viewOptions, pageNumber);

			byte[] bytes = pageStream.toByteArray();
			if (bytes.length == 0) {
				LOGGER.warn("Page {} of '{}' document has no data.", pageNumber, filePath);
			}
			Page page = createPage(pageNumber, bytes);

			return page;
		} catch (IOException e) {
			LOGGER.error("Exception throws while rendering png page: filePath={}, pageNumber={}", filePath, pageNumber, e);
			throw new ViewerUiException(e);
		}
	}

	@Override
	protected Thumb renderThumb(Viewer viewer, String filePath, int pageNumber) {
		try (ByteArrayOutputStream thumbStream = new ByteArrayOutputStream()) {
			PngViewOptions thumbViewOptions = createThumbViewOptions(thumbStream);
			viewer.view(thumbViewOptions, pageNumber);

			byte[] thumbBytes = thumbStream.toByteArray();
		if (thumbBytes.length == 0) {
			LOGGER.warn("Thumb for page {} of '{}' document has no data.", pageNumber, filePath);
		}
			Thumb thumb = createThumb(pageNumber, thumbBytes);
			return thumb;
		} catch (IOException e) {
			LOGGER.error("Exception throws while rendering thumb for png: filePath={}, pageNumber={}", filePath, pageNumber, e);
			throw new ViewerUiException(e);
		}
	}

	@Override
	protected ViewInfoOptions createViewInfoOptions() {
		return ViewInfoOptions.fromJpgViewOptions(_viewerConfig.getJpgViewOptions());
	}

	private PngViewOptions createPageViewOptions(OutputStream pageStream) {
		PngViewOptions viewOptions = new PngViewOptions(i -> pageStream, (i, closeable) -> {
		});

		CopyExtensions.copyViewOptions(_viewerConfig.getPngViewOptions(), viewOptions);

		return viewOptions;
	}

	private PngViewOptions createThumbViewOptions(OutputStream thumbStream) {
		PngViewOptions viewOptions = new PngViewOptions(i -> thumbStream,
				(i, closeable) -> { /*NOTE: Do nothing here*/ });

		final PngViewOptions pngViewOptions = _viewerConfig.getPngViewOptions();
		CopyExtensions.copyViewOptions(pngViewOptions, viewOptions);

		viewOptions.setMaxWidth(ThumbSettings.MAX_THUMB_WIDTH);
		viewOptions.setMaxHeight(ThumbSettings.MAX_THUMB_HEIGHT);

		return viewOptions;
	}
}
