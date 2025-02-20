package com.groupdocs.viewerui.ui.api.viewer;

import com.groupdocs.viewer.FileType;
import com.groupdocs.viewer.Viewer;
import com.groupdocs.viewer.ViewerSettings;
import com.groupdocs.viewer.logging.ConsoleLogger;
import com.groupdocs.viewer.options.LoadOptions;
import com.groupdocs.viewer.options.PdfViewOptions;
import com.groupdocs.viewer.options.ViewInfoOptions;
import com.groupdocs.viewer.results.PdfViewInfo;
import com.groupdocs.viewer.results.ViewInfo;
import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.api.FileTypeResolver;
import com.groupdocs.viewerui.ui.api.internalcaching.InternalCache;
import com.groupdocs.viewerui.ui.api.licensing.ViewerLicenser;
import com.groupdocs.viewerui.ui.configuration.InternalCacheOptions;
import com.groupdocs.viewerui.ui.configuration.ViewerConfig;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.IFileStorage;
import com.groupdocs.viewerui.ui.core.IViewer;
import com.groupdocs.viewerui.ui.core.PageFormatter;
import com.groupdocs.viewerui.ui.core.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.groupdocs.viewerui.ui.core.extensions.CopyExtensions.copyViewOptions;

public abstract class BaseViewer implements IViewer {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseViewer.class);
	private static final Map<String, WeakReference<Object>> _asyncLock = new ConcurrentHashMap<>();

	private final ViewerConfig _viewerConfig;

	private final ViewerLicenser _viewerLicenser;

	private final InternalCache _viewerCache;

	private final InternalCacheOptions _internalCacheOptions;

	private final FileStorageProvider _fileStorageProvider;

	private final FileTypeResolver _fileTypeResolver;

	private final PageFormatter _pageFormatter;

	private Viewer _viewer;

	protected BaseViewer(ViewerConfig viewerConfig, ViewerLicenser viewerLicenser, InternalCache viewerCache,
						 FileStorageProvider fileStorageProvider, FileTypeResolver fileTypeResolver, PageFormatter pageFormatter) {
		_viewerConfig = viewerConfig;
		_viewerLicenser = viewerLicenser;
		_viewerCache = viewerCache;
		_internalCacheOptions = viewerConfig.getInternalCacheOptions();
		_fileStorageProvider = fileStorageProvider;
		_fileTypeResolver = fileTypeResolver;
		_pageFormatter = pageFormatter;
	}

	private static DocumentInfo toDocumentInfo(ViewInfo viewInfo) {
		boolean printAllowed = true;
		if (viewInfo instanceof PdfViewInfo) {
			printAllowed = ((PdfViewInfo) viewInfo).isPrintingAllowed();
		}

		String fileType = viewInfo.getFileType().getExtension().replace(".", "");

		final DocumentInfo documentInfo = new DocumentInfo();
		documentInfo.setFileType(fileType);
		documentInfo.setPrintAllowed(printAllowed);
		final List<PageInfo> pageInfoList = viewInfo.getPages().stream().map(page -> {
			PageInfo pageInfo = new PageInfo();
			pageInfo.setNumber(page.getNumber());
			pageInfo.setWidth(page.getWidth());
			pageInfo.setHeight(page.getHeight());
			pageInfo.setName(page.getName());
			return pageInfo;
		}).collect(Collectors.toList());
		documentInfo.setPages(pageInfoList);
		return documentInfo;
	}

	public abstract String getPageExtension();

	public abstract String getThumbExtension();

	public abstract Page createPage(int pageNumber, byte[] data);

	public abstract Thumb createThumb(int pageNumber, byte[] data);

	protected abstract Page renderPage(Viewer viewer, String filePath, int pageNumber);

	protected abstract Thumb renderThumb(Viewer viewer, String filePath, int pageNumber);

	protected abstract ViewInfoOptions createViewInfoOptions();

	public DocumentInfo getDocumentInfo(FileCredentials fileCredentials) {
		Viewer viewer = initViewer(fileCredentials);
		ViewInfoOptions viewInfoOptions = createViewInfoOptions();
		ViewInfo viewInfo = viewer.getViewInfo(viewInfoOptions);

		DocumentInfo documentInfo = toDocumentInfo(viewInfo);
		return documentInfo;
	}

	public Page getPage(FileCredentials fileCredentials, int pageNumber) {
		Viewer viewer = initViewer(fileCredentials);
		Page page = renderPageInternal(viewer, fileCredentials, pageNumber);
		return page;
	}

	/**
	 * Retrieves a list of pages given file credentials and page numbers.
	 *
	 * @param fileCredentials The credentials of the file.
	 * @param pageNumbers     An array of page numbers to retrieve.
	 * @return A list of Page objects.
	 */
	public List<Page> getPages(FileCredentials fileCredentials, int[] pageNumbers) {
		Viewer viewer = initViewer(fileCredentials);

		final List<Page> pages = new ArrayList<>();
		for (int pageNumber : pageNumbers) {
			Page page = renderPageInternal(viewer, fileCredentials, pageNumber);
			pages.add(page);
		}

		return pages;
	}

	/**
	 * Retrieves a list of thumbs given file credentials and page numbers asynchronously.
	 *
	 * @param fileCredentials The credentials of the file.
	 * @param pageNumbers     An array of page numbers to retrieve.
	 * @return A list of Thumb objects.
	 */
	public List<Thumb> getThumbs(FileCredentials fileCredentials, int[] pageNumbers) {
		Viewer viewer = initViewer(fileCredentials);

		List<Thumb> thumbs = new ArrayList<>();
		for (int pageNumber : pageNumbers) {
			Thumb thumb = renderThumbInternal(viewer, fileCredentials, pageNumber);
			thumbs.add(thumb);
		}

		return thumbs;
	}

	public Thumb getThumb(FileCredentials fileCredentials, int pageNumber) {
		final Viewer viewer = initViewer(fileCredentials);
		Thumb thumb = renderThumbInternal(viewer, fileCredentials, pageNumber);
		return thumb;
	}

	public byte[] getPdf(FileCredentials fileCredentials) {
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			PdfViewOptions viewOptions = createPdfViewOptions(byteArrayOutputStream);

			final Viewer viewer = initViewer(fileCredentials);
			viewer.view(viewOptions);

			return byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			LOGGER.error("Exception throws while getting pdf: filePath={}", fileCredentials.getFilePath(), e);
			throw new ViewerUiException(e);
		}
	}

	public abstract byte[] getPageResource(FileCredentials fileCredentials, int pageNumber, String resourceName);

	private PdfViewOptions createPdfViewOptions(OutputStream pdfStream) {
		PdfViewOptions viewOptions = new PdfViewOptions(() -> pdfStream, closeable -> {
		});

		copyViewOptions(_viewerConfig.getPdfViewOptions(), viewOptions);

		return viewOptions;
	}

	private static void synchronizedBlock(String fileName, Runnable synchronizedBlock) {
		synchronized (getFileLock(fileName)) {
			synchronizedBlock.run();
		}
	}

	private Viewer createViewer(FileCredentials fileCredentials) {
		try (InputStream fileStream = createFileStream(fileCredentials.getFilePath())) {
			LoadOptions loadOptions = createLoadOptions(fileCredentials);

            return new Viewer(fileStream, loadOptions);
		}
		catch (IOException e) {
			LOGGER.error("Exception throws while creating a viewer: filePath={}", fileCredentials.getFilePath(), e);
			throw new ViewerUiException(e);
		}
	}

	private InputStream createFileStream(String filePath) {
		final IFileStorage fileStorage = _fileStorageProvider.provide();
		byte[] bytes = fileStorage.readFile(filePath);
		return new ByteArrayInputStream(bytes);
	}

	private LoadOptions createLoadOptions(FileCredentials fileCredentials) {
		FileType loadFileType = FileType.fromExtension(fileCredentials.getFileType());
		if (loadFileType == FileType.UNKNOWN) {
			loadFileType = _fileTypeResolver.resolveFileType(fileCredentials.getFilePath());
		}

		LoadOptions loadOptions = new LoadOptions();

		loadOptions.setFileType(FileType.fromExtension(loadFileType.getExtension()));
		loadOptions.setPassword(fileCredentials.getPassword());
		loadOptions.setResourceLoadingTimeout(3000);
		return loadOptions;
	}

	private Page renderPageInternal(Viewer viewer, FileCredentials fileCredentials, int pageNumber) {
		Page page = renderPage(viewer, fileCredentials.getFilePath(), pageNumber);
		page = _pageFormatter.format(fileCredentials, page);

		return page;
	}

	private Thumb renderThumbInternal(Viewer viewer, FileCredentials fileCredentials, int pageNumber) {
		Thumb thumb = renderThumb(viewer, fileCredentials.getFilePath(), pageNumber);

		return thumb;
	}

	private static synchronized Object getFileLock(String filename) {
		final Object lock = new Object();
		final Object actualLock = _asyncLock.computeIfAbsent(filename, k -> new WeakReference<>(lock)).get();// Retrieve the actual lock object
		return actualLock == null ? lock : actualLock;
	}

	private static void cleanupUnusedLocks() {
		_asyncLock.entrySet().removeIf(entry -> entry.getValue().get() == null);
	}

	private Viewer initViewer(FileCredentials fileCredentials) {
		if (_viewer != null) {
			return _viewer;
		}

		_viewerLicenser.setLicense();

		if (_internalCacheOptions.isCacheDisabled()) {
			_viewer = createViewer(fileCredentials);
			return _viewer;
		}

		String key = "VI__" + fileCredentials.getFilePath();
		synchronizedBlock(key, () -> {
			Viewer viewer = _viewerCache.get(fileCredentials);
			if (viewer != null) {
				_viewer = viewer;
			} else {
				_viewer = createViewer(fileCredentials);
				_viewerCache.set(fileCredentials, _viewer);
			}
		});

		return _viewer;
	}

	@Override
	public void close() {
		// NOTE: dispose when we're not going to reuse the object
		if (_internalCacheOptions.isCacheDisabled()) {
			if (_viewer != null) {
				_viewer.close();
				_viewer = null;
			}
		}
		cleanupUnusedLocks();
	}
}