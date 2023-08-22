package com.groupdocs.viewerui.ui.api.viewer;

import com.groupdocs.viewer.FileType;
import com.groupdocs.viewer.Viewer;
import com.groupdocs.viewer.options.LoadOptions;
import com.groupdocs.viewer.options.PdfViewOptions;
import com.groupdocs.viewer.options.ViewInfoOptions;
import com.groupdocs.viewer.results.PdfViewInfo;
import com.groupdocs.viewer.results.ViewInfo;
import com.groupdocs.viewerui.ui.configuration.ViewerConfig;
import com.groupdocs.viewerui.ui.configuration.InternalCacheOptions;
import com.groupdocs.viewerui.ui.api.IFileTypeResolver;
import com.groupdocs.viewerui.ui.api.internalcaching.IInternalCache;
import com.groupdocs.viewerui.ui.api.licensing.IViewerLicenser;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.IFileStorage;
import com.groupdocs.viewerui.ui.core.IPageFormatter;
import com.groupdocs.viewerui.ui.core.IViewer;
import com.groupdocs.viewerui.ui.core.entities.DocumentInfo;
import com.groupdocs.viewerui.ui.core.entities.FileCredentials;
import com.groupdocs.viewerui.ui.core.entities.Page;
import com.groupdocs.viewerui.ui.core.entities.PageInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.groupdocs.viewerui.ui.core.extensions.CopyExtensions.copyPdfViewOptions;

public abstract class BaseViewer implements IViewer {

	private final ViewerConfig _viewerConfig;

	private final IViewerLicenser _viewerLicenser;

	private final IInternalCache _viewerCache;

	private final InternalCacheOptions _internalCacheOptions;

	private final FileStorageProvider _fileStorageProvider;

	private final IFileTypeResolver _fileTypeResolver;

	private final IPageFormatter _pageFormatter;

	private Viewer _viewer;

	protected BaseViewer(ViewerConfig viewerConfig, IViewerLicenser viewerLicenser, IInternalCache viewerCache,
						 FileStorageProvider fileStorageProvider, IFileTypeResolver fileTypeResolver, IPageFormatter pageFormatter) {
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

	public abstract Page createPage(int pageNumber, byte[] data);

	protected abstract Page renderPage(Viewer viewer, String filePath, int pageNumber);

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

	public List<Page> getPages(FileCredentials fileCredentials, int[] pageNumbers) {
		Viewer viewer = initViewer(fileCredentials);

		final List<Page> pages = new ArrayList<>();
		for (int pageNumber : pageNumbers) {
			Page page = renderPageInternal(viewer, fileCredentials, pageNumber);
			pages.add(page);
		}

		return pages;
	}

	public byte[] getPdf(FileCredentials fileCredentials) {
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			PdfViewOptions viewOptions = createPdfViewOptions(byteArrayOutputStream);

			Viewer viewer = initViewer(fileCredentials);
			viewer.view(viewOptions);

			return byteArrayOutputStream.toByteArray();
		}
		catch (IOException e) {
			e.printStackTrace(); // TODO: Add logging
			throw new RuntimeException(e);
		}
	}

	public abstract byte[] getPageResource(FileCredentials fileCredentials, int pageNumber, String resourceName);

	private PdfViewOptions createPdfViewOptions(OutputStream pdfStream) {
		PdfViewOptions viewOptions = new PdfViewOptions(() -> pdfStream, closeable -> {
		});

		copyPdfViewOptions(_viewerConfig.getPdfViewOptions(), viewOptions);

		return viewOptions;
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

		// String key = "VI__" + fileCredentials.getFilePath();
		// asyncLock.Lock(key) {
		Viewer viewer = _viewerCache.get(fileCredentials);
		if (viewer != null) {
			_viewer = viewer;
		}
		else {
			_viewer = createViewer(fileCredentials);
			_viewerCache.set(fileCredentials, _viewer);
		}
		// }

		return _viewer;
	}

	private Viewer createViewer(FileCredentials fileCredentials) {
		try (InputStream fileStream = createFileStream(fileCredentials.getFilePath())) {
			LoadOptions loadOptions = createLoadOptions(fileCredentials);
			Viewer viewer = new Viewer(fileStream, loadOptions);

			return viewer;
		}
		catch (IOException e) {
			e.printStackTrace(); // TODO: Add logging
			throw new RuntimeException(e);
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

	@Override
	public void close() {
		// NOTE: dispose when we're not going to reuse the object
		if (_internalCacheOptions.isCacheDisabled()) {
			if (_viewer != null) {
				_viewer.close();
				_viewer = null;
			}
		}
	}

}
