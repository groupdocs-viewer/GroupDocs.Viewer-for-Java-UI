package com.groupdocs.viewerui.ui.api.viewer;

import com.groupdocs.viewer.Viewer;
import com.groupdocs.viewer.options.HtmlViewOptions;
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
		return HtmlPage.DEFAULT_EXTENSION;
	}

	@Override
	public String getThumbExtension() {
		return JpgThumb.DEFAULT_EXTENSION;
	}

	@Override
	public Page createPage(int pageNumber, byte[] data) {
		return new HtmlPage(pageNumber, data);
	}

	@Override
	public Thumb createThumb(int pageNumber, byte[] data) {
		return new JpgThumb(pageNumber, data);
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
			HtmlViewOptions viewOptions = createPageViewOptions(byteArrayOutputStream);

			viewer.view(viewOptions, pageNumber);

			byte[] bytes = byteArrayOutputStream.toByteArray();
			if (bytes.length == 0) {
				LOGGER.warn("Page {} of '{}' document has no data.", pageNumber, filePath);
			}
			Page page = createPage(pageNumber, bytes);

			return page;
		} catch (Exception e) {

			LOGGER.error("Exception throws while rendering html page with embedded resources: filePath={}, pageNumber={}, viewer={}", filePath, pageNumber, this, e);
			throw new ViewerUiException(e);
		}
	}

	@Override
	protected Thumb renderThumb(Viewer viewer, String filePath, int pageNumber) {
		try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			JpgViewOptions thumbViewOptions = createThumbViewOptions(byteArrayOutputStream);

			viewer.view(thumbViewOptions, pageNumber);

			byte[] thumbBytes = byteArrayOutputStream.toByteArray();
		if (thumbBytes.length == 0) {
			LOGGER.warn("Thumb for page {} of '{}' document has no data.", pageNumber, filePath);
		}

			Thumb thumb = createThumb(pageNumber, thumbBytes);

			return thumb;
		} catch (IOException e) {
			LOGGER.error("Exception throws while rendering thumb for html with embedded resources: filePath={}, pageNumber={}", filePath, pageNumber, e);
			throw new ViewerUiException(e);
		}
	}

	private HtmlViewOptions createPageViewOptions(OutputStream pageStream) {
		final HtmlViewOptions viewOptions = HtmlViewOptions.forEmbeddedResources(i -> pageStream,
				(i, closeable) -> { /*NOTE: Do nothing here*/ });

		CopyExtensions.copyViewOptions(_viewerConfig.getHtmlViewOptions(), viewOptions);

		return viewOptions;
	}

	private JpgViewOptions createThumbViewOptions(OutputStream pageStream) {
		final JpgViewOptions viewOptions = new JpgViewOptions(i -> pageStream,
				(i, closeable) -> { /*NOTE: Do nothing here*/ });

		CopyExtensions.copyBaseViewOptions(_viewerConfig.getHtmlViewOptions(), viewOptions);
		viewOptions.setExtractText(false);
		viewOptions.setQuality((byte) ThumbSettings.THUMB_QUALITY);
		viewOptions.setMaxWidth(ThumbSettings.MAX_THUMB_WIDTH);
		viewOptions.setMaxHeight(ThumbSettings.MAX_THUMB_HEIGHT);

		return viewOptions;
	}

}
