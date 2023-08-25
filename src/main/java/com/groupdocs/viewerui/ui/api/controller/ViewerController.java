package com.groupdocs.viewerui.ui.api.controller;

import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.api.FileNameResolver;
import com.groupdocs.viewerui.ui.api.SearchTermResolver;
import com.groupdocs.viewerui.ui.api.UiConfigProvider;
import com.groupdocs.viewerui.ui.api.infrastructure.ViewerActionResult;
import com.groupdocs.viewerui.ui.api.models.*;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.IFileStorage;
import com.groupdocs.viewerui.ui.core.IViewer;
import com.groupdocs.viewerui.ui.core.configuration.Config;
import com.groupdocs.viewerui.ui.core.entities.*;
import com.groupdocs.viewerui.ui.core.extensions.StringExtensions;
import org.apache.commons.io.IOUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ViewerController implements Closeable {

    public static final String JSON_CONTENT_TYPE = "application/json";
    private final FileNameResolver _fileNameResolver;
    private final SearchTermResolver _searchTermResolver;
    private final UiConfigProvider _uiConfigProvider;
    private final IViewer _viewer;
    // private final ILogger<ViewerController> _logger;
    private final Config _config;
    private FileStorageProvider _fileStorageProvider;

    public ViewerController(FileStorageProvider fileStorageProvider, FileNameResolver fileNameResolver,
                            SearchTermResolver searchTermResolver, UiConfigProvider uiConfigProvider, IViewer viewer, Config config
            /* ILogger<ViewerController> logger */) {
        _fileStorageProvider = fileStorageProvider;
        _fileNameResolver = fileNameResolver;
        _searchTermResolver = searchTermResolver;
        _viewer = viewer;
        // _logger = logger;
        _config = config;
        _uiConfigProvider = uiConfigProvider;
    }

    public ViewerActionResult loadFileTree(LoadFileTreeRequest request) {
        if (!_config.isBrowse()) {
            return errorJsonResult("Browsing files is disabled.");
        }

        try {
            final IFileStorage fileStorage = _fileStorageProvider.provide();
            List<FileSystemEntry> files = fileStorage.listDirsAndFiles(request.getPath());

            return okJsonResult(files.stream()
                    .map(entity -> new FileDescription(entity.getFilePath(), entity.getFilePath(), entity.isDirectory(),
                            entity.getSize())).toList());
        } catch (Exception ex) {
            // _logger.LogError(ex, "Failed to load file tree.");

            return errorJsonResult(ex.getMessage());
        }
    }

    public ViewerActionResult loadDocumentDescription(LoadDocumentDescriptionRequest request) {
        try {
            FileCredentials fileCredentials = new FileCredentials(
                    request.getGuid(),
                    request.getFileType(),
                    request.getPassword());
            DocumentInfo documentDescription = _viewer.getDocumentInfo(fileCredentials);

            int[] pageNumbers = getPageNumbers(documentDescription.getPages().size());
            List<Page> pagesData = _viewer.getPages(fileCredentials, pageNumbers);

            List<PageDescription> pages = new ArrayList<>();
            final String searchTerm = _searchTermResolver.resolveSearchTerm(request.getGuid());
            for (PageInfo pageInfo : documentDescription.getPages()) {
                final Optional<Page> pageData = pagesData.stream()
                        .filter(page -> page.getPageNumber() == pageInfo.getNumber())
                        .findFirst();
                String content = null;
                if (pageData.isPresent()) {
                    content = pageData.get().getContent();
                }
                PageDescription pageDescription = new PageDescription(pageInfo.getWidth(), pageInfo.getHeight(),
                        pageInfo.getName(), pageInfo.getNumber(), content);

                pages.add(pageDescription);
            }

            LoadDocumentDescriptionResponse result = new LoadDocumentDescriptionResponse(request.getGuid(),
                    documentDescription.getFileType(), documentDescription.isPrintAllowed(), pages, searchTerm);

            return okJsonResult(result);
        } catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("password")) {
                String message = request.getPassword() == null || request.getPassword().isEmpty() ? "Password Required"
                        : "Incorrect Password";

                return forbiddenJsonResult(message);
            }

            // _logger.LogError(ex, "Failed to read document description.");

            return errorJsonResult(ex.getMessage());
        }
    }

    public ViewerActionResult loadDocumentPageResource(LoadDocumentPageResourceRequest request) {
        if (!_config.isHtmlMode()) {
            return errorJsonResult("Loading page resources is disabled in image mode.");
        }

        try {
            FileCredentials fileCredentials = new FileCredentials(request.getGuid(), request.getFileType(),
                    request.getPassword());
            byte[] bytes = _viewer.getPageResource(fileCredentials, request.getPageNumber(), request.getResourceName());

            if (bytes == null || bytes.length == 0) {
                return notFoundJsonResult("Resource " + request.getResourceName() + " was not found");
            }

            String contentType = StringExtensions.contentTypeFromFileName(request.getResourceName());

            return new ViewerActionResult(contentType, HttpURLConnection.HTTP_OK, bytes);
        } catch (Exception ex) {
            // _logger.LogError(ex, "Failed to load document page resource.");

            return errorJsonResult(ex.getMessage());
        }
    }

    public ViewerActionResult downloadDocument(String path) {
        if (!_config.isDownload()) {
            return errorJsonResult("Downloading files is disabled.");
        }
        final IFileStorage fileStorage = _fileStorageProvider.provide();

        try {
            String fileName = _fileNameResolver.resolveFileName(path);
            byte[] bytes = fileStorage.readFile(path);

            return new ViewerActionResult("application/octet-stream", HttpURLConnection.HTTP_OK, new FileResponse(bytes, fileName));
        } catch (Exception ex) {
            // _logger.LogError(ex, "Failed to download a document.");

            return errorJsonResult(ex.getMessage());
        }
    }

    public ViewerActionResult uploadDocument(String fileNameOrUrl, InputStream inputStream, boolean isRewrite) {
        if (!_config.isUpload()) {
            return errorJsonResult("Uploading files is disabled.");
        }
        final IFileStorage fileStorage = _fileStorageProvider.provide();

        try {
            byte[] bytes = readOrDownloadFile(fileNameOrUrl, inputStream);

            String filePath = fileStorage.writeFile(fileNameOrUrl, bytes, isRewrite);

            UploadFileResponse result = new UploadFileResponse(filePath);

            return okJsonResult(result);
        } catch (Exception ex) {
            // _logger.LogError(ex, "Failed to upload document.");

            return errorJsonResult(ex.getMessage());
        }
    }

    public ViewerActionResult printPdf(PrintPdfRequest request) {
        if (!_config.isPrint()) {
            return errorJsonResult("Printing files is disabled.");
        }

        try {
            FileCredentials fileCredentials = new FileCredentials(request.getGuid(), request.getFileType(),
                    request.getPassword());

            String fileName = _fileNameResolver.resolveFileName(request.getGuid());
            String pdfFileName = StringExtensions.changeExtension(fileName, ".pdf");
            byte[] pdfFileBytes = _viewer.getPdf(fileCredentials);

            return new ViewerActionResult("application/pdf", HttpURLConnection.HTTP_OK, new FileResponse(pdfFileBytes, pdfFileName));
        } catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("password")) {
                String message = request.getPassword() == null || request.getPassword().isEmpty() ? "Password Required" : "Incorrect Password";

                return forbiddenJsonResult(message);
            }

            // _logger.LogError(ex, "Failed to create PDF file.");

            return errorJsonResult(ex.getMessage());
        }
    }

    private int[] getPageNumbers(int totalPageCount) {
        if (_config.getPreloadPageCount() == 0) {
            return IntStream.rangeClosed(1, totalPageCount).toArray();
        }

        int pageCount = Math.min(totalPageCount, _config.getPreloadPageCount());

        return IntStream.rangeClosed(1, pageCount).toArray();
    }

    public ViewerActionResult loadDocumentPages(LoadDocumentPagesRequest request) {
        try {
            FileCredentials fileCredentials = new FileCredentials(
                    request.getGuid(), request.getFileType(), request.getPassword());
            List<Page> pages = _viewer.getPages(fileCredentials, request.getPages());
            List<PageContent> pageContents = pages.stream()
                    .map(page -> new PageContent(page.getPageNumber(), page.getContent()))
                    .collect(Collectors.toList());

            return okJsonResult(pageContents);
        } catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("password")) {
                String message = request.getPassword() == null || request.getPassword().isEmpty() ? "Password Required"
                        : "Incorrect Password";

                return forbiddenJsonResult(message);
            }

            // _logger.LogError(ex, "Failed to retrieve document pages.");

            return errorJsonResult(ex.getMessage());
        }
    }

    public ViewerActionResult loadDocumentPage(LoadDocumentPageRequest request) {
        try {
            FileCredentials fileCredentials = new FileCredentials(request.getGuid(), request.getFileType(),
                    request.getPassword());
            Page page = _viewer.getPage(fileCredentials, request.getPage());
            PageContent pageContent = new PageContent(page.getPageNumber(), page.getContent());

            return okJsonResult(pageContent);
        } catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("password")) {
                String message = request.getPassword() == null || request.getPassword().isEmpty() ? "Password Required"
                        : "Incorrect Password";

                return forbiddenJsonResult(message);
            }

            // _logger.LogError(ex, "Failed to retrieve document page.");

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
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new ViewerUiException("Can't read stream!", e);
        }
    }

    private byte[] downloadFile(String url) {
        try {
            final URL urlClient = new URL(url);
            return IOUtils.toByteArray(urlClient.openStream());
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

    @Override
    public void close() {
        if (this._viewer != null) {
            this._viewer.close();
        }
    }
}
