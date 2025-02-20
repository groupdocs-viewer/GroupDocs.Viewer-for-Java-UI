package com.groupdocs.viewerui.ui.api.controller;

import com.groupdocs.viewer.utils.PathUtils;
import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.api.FileNameResolver;
import com.groupdocs.viewerui.ui.api.SearchTermResolver;
import com.groupdocs.viewerui.ui.api.infrastructure.ViewerActionResult;
import com.groupdocs.viewerui.ui.api.models.*;
import com.groupdocs.viewerui.ui.api.utils.IApiUrlBuilder;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.IFileStorage;
import com.groupdocs.viewerui.ui.core.IViewer;
import com.groupdocs.viewerui.ui.core.configuration.Config;
import com.groupdocs.viewerui.ui.core.configuration.RenderingMode;
import com.groupdocs.viewerui.ui.core.entities.*;
import com.groupdocs.viewerui.ui.core.extensions.StreamExtensions;
import com.groupdocs.viewerui.ui.core.extensions.StringExtensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ViewerController implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewerController.class);
    public static final String JSON_CONTENT_TYPE = "application/json";
    private final FileNameResolver _fileNameResolver;
    private final SearchTermResolver _searchTermResolver;
    private final IApiUrlBuilder _apiUrlBuilder;
    private final IViewer _viewer;
    private final Config _config;
    private FileStorageProvider _fileStorageProvider;

    public ViewerController(FileStorageProvider fileStorageProvider, FileNameResolver fileNameResolver,
                            SearchTermResolver searchTermResolver, IViewer viewer, Config config, IApiUrlBuilder apiUrlBuilder) {
        _fileStorageProvider = fileStorageProvider;
        _fileNameResolver = fileNameResolver;
        _searchTermResolver = searchTermResolver;
        _viewer = viewer;
        _config = config;
        this._apiUrlBuilder = apiUrlBuilder;
    }

    public ViewerActionResult listDir(LoadFileTreeRequest request) {
        if (!_config.isEnableFileBrowser()) {
            return errorJsonResult("Browsing files is disabled.");
        }

        final String path = request.getPath();
        try {
            final IFileStorage fileStorage = _fileStorageProvider.provide();
            List<FileSystemEntry> files = fileStorage.listDirsAndFiles(path);

            return okJsonResult(files.stream()
                    .map(entity -> new FileDescription(entity.getFilePath(), entity.getFilePath(), entity.isDirectory(),
                            entity.getSize())).collect(Collectors.toList()));
        } catch (Exception e) {
            LOGGER.error("Exception throws while loading file tree: path={}", path, e);
            return errorJsonResult(e.getMessage());
        }
    }

    public ViewerActionResult viewData(ViewDataRequest request) {
        final String guid = request.getFile();
        try {
            FileCredentials fileCredentials = new FileCredentials(
                    guid,
                    request.getFileType(),
                    request.getPassword());
            DocumentInfo documentInfo = _viewer.getDocumentInfo(fileCredentials);

            int[] pagesToCreate = getPagesToCreate(documentInfo.getPages().size(), _config.getPreloadPages());
            List<PageData> pages = createViewDataPages(fileCredentials, documentInfo, pagesToCreate);

            final String searchTerm = _searchTermResolver.resolveSearchTerm(guid);
            final String fileName = _fileNameResolver.resolveFileName(request.getFile());
            ViewDataResponse result = new ViewDataResponse(
                    request.getFile(),
                    documentInfo.getFileType(),
                    fileName == null || fileName.trim().isEmpty()
                            ? PathUtils.getFileName(request.getFile())
                            : fileName,
                    documentInfo.isPrintAllowed(),
                    searchTerm,
                    pages);

            return okJsonResult(result);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase(Locale.ROOT).contains("password")) {
                String message = request.getPassword() == null || request.getPassword().isEmpty() ? "Password Required"
                        : "Incorrect Password";

                return forbiddenJsonResult(message);
            }
            LOGGER.error("Exception throws while loading document description: guid={}", guid, e);
            return errorJsonResult(e.getMessage());
        }
    }

    public ViewerActionResult getResource(GetResourceRequest request) {
        if (_config.getRenderingMode() != RenderingMode.Html) {
            return errorJsonResult("Loading page resources is disabled in image mode.");
        }

        final String guid = request.getGuid();
        try {
            FileCredentials fileCredentials = new FileCredentials(guid, request.getFileType(),
                    request.getPassword());
            byte[] bytes = _viewer.getPageResource(fileCredentials, request.getPageNumber(), request.getResourceName());

            if (bytes == null || bytes.length == 0) {
                return notFoundJsonResult("Resource " + request.getResourceName() + " was not found");
            }

            String contentType = StringExtensions.contentTypeFromFileName(request.getResourceName());

            return new ViewerActionResult(contentType, bytes.length, HttpURLConnection.HTTP_OK, bytes);
        } catch (Exception e) {
            LOGGER.error("Exception throws while loading document page resource: guid={}", guid, e);
            return errorJsonResult(e.getMessage());
        }
    }

    public ViewerActionResult uploadFile(String fileNameOrUrl, InputStream inputStream, boolean isRewrite) {
        if (!_config.isEnableFileUpload()) {
            return errorJsonResult("Uploading files is disabled.");
        }
        final IFileStorage fileStorage = _fileStorageProvider.provide();

        try {
            byte[] bytes = readOrDownloadFile(fileNameOrUrl, inputStream);

            String filePath = fileStorage.writeFile(fileNameOrUrl, bytes, isRewrite);

            UploadFileResponse result = new UploadFileResponse(filePath);

            return okJsonResult(result);
        } catch (Exception e) {
            LOGGER.error("Exception throws while uploading document: fileNameOrUrl={}", fileNameOrUrl, e);
            return errorJsonResult(e.getMessage());
        }
    }

    public ViewerActionResult createPdf(CreatePdfRequest request) {
        if (!_config.isEnableDownloadPdf() && !_config.isEnablePrint()) {
            return errorJsonResult("Creating PDF files is disabled.");
        }

        final String file = request.getFile();
        try {
            FileCredentials fileCredentials = new FileCredentials(file, request.getFileType(), request.getPassword());

            _viewer.getPdf(fileCredentials);

            final CreatePdfResponse createPdfResponse = new CreatePdfResponse();
            final String pdfUrl = _apiUrlBuilder.buildPdfUrl(request.getFile());
            createPdfResponse.setPdfUrl(pdfUrl);

            return okJsonResult(createPdfResponse);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase(Locale.ROOT).contains("password")) {
                String message = request.getPassword() == null || request.getPassword().isEmpty() ? "Password Required" : "Incorrect Password";

                return forbiddenJsonResult(message);
            }
            LOGGER.error("Exception throws while printing pdf: file={}", file, e);
            return errorJsonResult(e.getMessage());
        }
    }

    public ViewerActionResult getPage(GetPageRequest request) {
        try {
            FileCredentials fileCredentials = new FileCredentials(request.getFile());
            Page page = _viewer.getPage(fileCredentials, request.getPage());

            byte[] bytes = page.getPageData();

            return new ViewerActionResult(page.getContentType(), bytes.length, HttpURLConnection.HTTP_OK, bytes);
        } catch (Exception e) {
            LOGGER.error(String.format("Failed to retrieve document page, file: '%s', page: %s ", request.getFile(), request.getPage()), e);
            return errorJsonResult(e.getMessage());
        }
    }

    public ViewerActionResult getThumb(GetThumbRequest request) {
        try {
            FileCredentials fileCredentials = new FileCredentials(request.getFile());
            Thumb thumb = _viewer.getThumb(fileCredentials, request.getPage());

            final byte[] bytes = thumb.getThumbData();
            return new ViewerActionResult(thumb.getContentType(), bytes.length, HttpURLConnection.HTTP_OK, bytes);
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve document thumb", e);
            return errorJsonResult(e.getMessage());
        }
    }

    public ViewerActionResult getPdf(GetPdfRequest request) {
        if (!_config.isEnableDownloadPdf() && !_config.isEnablePrint()) {
            return errorJsonResult("Creating PDF files is disabled.");
        }

        try {
            FileCredentials fileCredentials = new FileCredentials(request.getFile());

            String fileName = _fileNameResolver.resolveFileName(request.getFile());
            String pdfFileName = StringExtensions.changeExtension(fileName, ".pdf");
            byte[] bytes = _viewer.getPdf(fileCredentials);

            return new ViewerActionResult("application/pdf", bytes.length, HttpURLConnection.HTTP_OK, new FileResponse(bytes, pdfFileName));
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve PDF file.", e);

            return errorJsonResult(e.getMessage());
        }
    }

    // NOTE: This method returns all of the pages including created and not
    private List<PageData> createViewDataPages(FileCredentials file, DocumentInfo docInfo, int[] pagesToCreate) {
        _viewer.getPages(file, pagesToCreate);

        if (_config.isEnableThumbnails()) {
            _viewer.getThumbs(file, pagesToCreate);
        }

        List<PageData> pages = new ArrayList<>();
        for (PageInfo page : docInfo.getPages()) {
            boolean isPageCreated = Arrays.stream(pagesToCreate).anyMatch(p -> p == page.getNumber());
            if (isPageCreated) {
                final String pageUrl = _apiUrlBuilder.buildPageUrl(file.getFilePath(), page.getNumber(), _viewer.getPageExtension());
                final String thumbUrl = _apiUrlBuilder.buildThumbUrl(file.getFilePath(), page.getNumber(), _viewer.getPageExtension());

                final PageData pageData = _config.isEnableThumbnails()
                        ? new PageData(page.getNumber(), page.getWidth(), page.getHeight(), pageUrl, thumbUrl)
                        : new PageData(page.getNumber(), page.getWidth(), page.getHeight(), pageUrl);

                pages.add(pageData);
            } else {
                pages.add(new PageData(page.getNumber(), page.getWidth(), page.getHeight()));
            }
        }

        return pages;
    }

    private int[] getPagesToCreate(int totalPageCount, int preloadPageCount) {
        if (preloadPageCount == 0) {
            return IntStream.rangeClosed(1, totalPageCount).toArray();
        }

        int pageCount = Math.min(totalPageCount, preloadPageCount);

        return IntStream.rangeClosed(1, pageCount).toArray();
    }

    public ViewerActionResult createPages(CreatePagesRequest request) {
        final String guid = request.getFile();
        try {
            FileCredentials file = new FileCredentials(request.getFile(), request.getFileType(), request.getPassword());

            DocumentInfo docInfo = _viewer.getDocumentInfo(file);

            List<PageData> pages = createPagesAndThumbs(file, docInfo, request.getPages());

            return okJsonResult(pages);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase(Locale.ROOT).contains("password")) {
                String message = request.getPassword() == null || request.getPassword().isEmpty() ? "Password Required"
                        : "Incorrect Password";

                return forbiddenJsonResult(message);
            }
            LOGGER.error("Exception throws while loading document pages: guid={}", guid, e);
            return errorJsonResult(e.getMessage());
        }
    }

    // NOTE: This method returns only created pages
    private List<PageData> createPagesAndThumbs(FileCredentials file, DocumentInfo docInfo, int[] pagesToCreate) {
        _viewer.getPages(file, pagesToCreate);

        if (_config.isEnableThumbnails()) {
            _viewer.getThumbs(file, pagesToCreate);
        }

        List<PageData> pages = new ArrayList<>();
        for (int pageNumber : pagesToCreate) {
            final Optional<PageInfo> pageInfoOptional = docInfo.getPages().stream().filter(p -> p.getNumber() == pageNumber).findFirst();
            if (pageInfoOptional.isPresent()) {
                PageInfo page = pageInfoOptional.get();
                final String pageUrl = _apiUrlBuilder.buildPageUrl(file.getFilePath(), page.getNumber(), _viewer.getPageExtension());
                final String thumbUrl = _apiUrlBuilder.buildThumbUrl(file.getFilePath(), page.getNumber(), _viewer.getThumbExtension());

                final PageData pageData = _config.isEnableThumbnails()
                        ? new PageData(page.getNumber(), page.getWidth(), page.getHeight(), pageUrl, thumbUrl)
                        : new PageData(page.getNumber(), page.getWidth(), page.getHeight(), pageUrl);

                pages.add(pageData);
            } else {
                LOGGER.warn("Page {} was not found in collection.", pageNumber);
            }
        }

        return pages;
    }

    public ViewerActionResult loadDocumentPage(LoadDocumentPageRequest request) {
        final String guid = request.getGuid();
        try {
            FileCredentials fileCredentials = new FileCredentials(guid, request.getFileType(),
                    request.getPassword());
            Page page = _viewer.getPage(fileCredentials, request.getPage());
            PageContent pageContent = new PageContent(page.getPageNumber(), page.getContent());

            return okJsonResult(pageContent);
        } catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().toLowerCase(Locale.ROOT).contains("password")) {
                String message = request.getPassword() == null || request.getPassword().isEmpty() ? "Password Required"
                        : "Incorrect Password";

                return forbiddenJsonResult(message);
            }
            LOGGER.error("Exception throws while loading document page: guid={}", guid);
            return errorJsonResult(ex.getMessage());
        }
    }

    private byte[] readOrDownloadFile(String fileNameOrUrl, InputStream inputStream) {
        return (fileNameOrUrl == null || !fileNameOrUrl.startsWith("http")) ? readFileFromRequest(inputStream)
                : downloadFile(fileNameOrUrl);
    }

    private byte[] readFileFromRequest(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        try {
            return StreamExtensions.toByteArray(inputStream);
        } catch (IOException e) {
            throw new ViewerUiException("Can't read stream!", e);
        }
    }

    private byte[] downloadFile(String url) {
        try {
            final URL urlClient = new URL(url);
            return StreamExtensions.toByteArray(urlClient.openStream());
        } catch (Exception e) {
            throw new ViewerUiException("Can't download file!", e);
        }
    }

    private ViewerActionResult errorJsonResult(String message) {
        return new ViewerActionResult(
                JSON_CONTENT_TYPE,
                HttpURLConnection.HTTP_INTERNAL_ERROR,
                new ErrorResponse(message)
        );

        // StatusCode = StatusCodes.Status500InternalServerError
    }

    private ViewerActionResult forbiddenJsonResult(String message) {
        return new ViewerActionResult(
                JSON_CONTENT_TYPE,
                HttpURLConnection.HTTP_FORBIDDEN,
                new ErrorResponse(message)
        );
        // StatusCode = StatusCodes.Status403Forbidden
    }

    private ViewerActionResult notFoundJsonResult(String message) {
        return new ViewerActionResult(
                JSON_CONTENT_TYPE,
                HttpURLConnection.HTTP_NOT_FOUND,
                new ErrorResponse(message)
        );

        // StatusCode = StatusCodes.Status404NotFound
    }

    private ViewerActionResult okJsonResult(Object result) {
        return new ViewerActionResult(
                JSON_CONTENT_TYPE,
                HttpURLConnection.HTTP_OK,
                result
        );
    }

    public FileStorageProvider getFileStorageProvider() {
        return _fileStorageProvider;
    }

    public void setFileStorageProvider(FileStorageProvider fileStorageProvider) {
        this._fileStorageProvider = fileStorageProvider;
    }

    public IApiUrlBuilder getApiUrlBuilder() {
        return _apiUrlBuilder;
    }

    @Override
    public void close() {
        if (this._viewer != null) {
            this._viewer.close();
        }
    }
}
