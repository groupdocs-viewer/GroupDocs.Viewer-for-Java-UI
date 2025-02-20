package com.groupdocs.viewerui.ui.api.viewer;

import com.groupdocs.viewer.Viewer;
import com.groupdocs.viewer.options.JpgViewOptions;
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

public class JpgViewer extends BaseViewer {
	private static final Logger LOGGER = LoggerFactory.getLogger(JpgViewer.class);

	private final ViewerConfig _viewerConfig;

	public JpgViewer(ViewerConfig viewerConfig, ViewerLicenser licenser, InternalCache internalCache,
					 FileStorageProvider fileStorageProvider, FileTypeResolver fileTypeResolver, PageFormatter pageFormatter) {
		super(viewerConfig, licenser, internalCache, fileStorageProvider, fileTypeResolver, pageFormatter);
		_viewerConfig = viewerConfig;
	}

	@Override
	public String getPageExtension() {
		return JpgPage.DEFAULT_EXTENSION;
	}

	@Override
	public String getThumbExtension() {
		return JpgThumb.DEFAULT_EXTENSION;
	}

	@Override
	public Page createPage(int pageNumber, byte[] data) {
		return new JpgPage(pageNumber, data);
	}

	@Override
	public Thumb createThumb(int pageNumber, byte[] data) {
		return new JpgThumb(pageNumber, data);
	}

	@Override
	public byte[] getPageResource(FileCredentials fileCredentials, int pageNumber, String resourceName) {
		throw new ViewerUiException("JpgViewer does not support retrieving external HTML resources.");
	}

	@Override
	protected Page renderPage(Viewer viewer, String filePath, int pageNumber) {
		try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			JpgViewOptions viewOptions = createPageViewOptions(byteArrayOutputStream);
			viewer.view(viewOptions, pageNumber);

			byte[] bytes = byteArrayOutputStream.toByteArray();
			if (bytes.length == 0) {
				LOGGER.warn("Page {} of '{}' document has no data.", pageNumber, filePath);
			}
			Page page = createPage(pageNumber, bytes);

			return page;
		} catch (IOException e) {
			LOGGER.error("Exception throws while rendering jpg page: filePath={}", filePath, e);
			throw new ViewerUiException(e);
		}
	}

	@Override
	protected Thumb renderThumb(Viewer viewer, String filePath, int pageNumber) {
		try (final ByteArrayOutputStream thumbStream = new ByteArrayOutputStream()) {
			JpgViewOptions thumbViewOptions = createThumbViewOptions(thumbStream);
			viewer.view(thumbViewOptions, pageNumber);

			byte[] thumbBytes = thumbStream.toByteArray();
		if (thumbBytes.length == 0) {
			LOGGER.warn("Thumb for page {} of '{}' document has no data.", pageNumber, filePath);
		}

			Thumb thumb = createThumb(pageNumber, thumbBytes);
			return thumb;
		} catch (IOException e) {
			LOGGER.error("Exception throws while rendering jpg thumb: filePath={}", filePath, e);
			throw new ViewerUiException(e);
		}
	}

	@Override
	protected ViewInfoOptions createViewInfoOptions() {
		return ViewInfoOptions.fromJpgViewOptions(_viewerConfig.getJpgViewOptions());
	}

	private JpgViewOptions createPageViewOptions(OutputStream pageStream) {
		JpgViewOptions viewOptions = new JpgViewOptions(i -> pageStream, (i, closeable) -> {

		});

		CopyExtensions.copyViewOptions(_viewerConfig.getJpgViewOptions(), viewOptions);

		return viewOptions;
	}

	private JpgViewOptions createThumbViewOptions(OutputStream thumbStream) {
		JpgViewOptions viewOptions = new JpgViewOptions(i -> thumbStream,
				(i, closeable) -> { /*NOTE: Do nothing here*/ });

		CopyExtensions.copyViewOptions(_viewerConfig.getJpgViewOptions(), viewOptions);
		viewOptions.setQuality((byte) ThumbSettings.THUMB_QUALITY);
		viewOptions.setMaxWidth(ThumbSettings.MAX_THUMB_WIDTH);
		viewOptions.setMaxHeight(ThumbSettings.MAX_THUMB_HEIGHT);

		return viewOptions;
	}

}
