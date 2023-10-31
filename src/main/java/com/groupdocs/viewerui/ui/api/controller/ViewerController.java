package com.groupdocs.viewerui.ui.api.controller;

import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.api.FileNameResolver;
import com.groupdocs.viewerui.ui.api.SearchTermResolver;
import com.groupdocs.viewerui.ui.api.infrastructure.ViewerActionResult;
import com.groupdocs.viewerui.ui.api.models.*;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.IFileStorage;
import com.groupdocs.viewerui.ui.core.IViewer;
import com.groupdocs.viewerui.ui.core.configuration.Config;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ViewerController implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewerController.class);
    public static final String JSON_CONTENT_TYPE = "application/json";
    private final FileNameResolver _fileNameResolver;
    private final SearchTermResolver _searchTermResolver;
    private final IViewer _viewer;
    private final Config _config;
    private FileStorageProvider _fileStorageProvider;

    public ViewerController(FileStorageProvider fileStorageProvider, FileNameResolver fileNameResolver,
                            SearchTermResolver searchTermResolver, IViewer viewer, Config config) {
        _fileStorageProvider = fileStorageProvider;
        _fileNameResolver = fileNameResolver;
        _searchTermResolver = searchTermResolver;
        _viewer = viewer;
        _config = config;
    }

    public ViewerActionResult loadFileTree(LoadFileTreeRequest request) {
        if (!_config.isBrowse()) {
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

    public ViewerActionResult loadDocumentDescription(LoadDocumentDescriptionRequest request) {
        final String guid = request.getGuid();
        try {
            FileCredentials fileCredentials = new FileCredentials(
                    guid,
                    request.getFileType(),
                    request.getPassword());
            DocumentInfo documentDescription = _viewer.getDocumentInfo(fileCredentials);

            int[] pageNumbers = getPageNumbers(documentDescription.getPages().size());
            List<Page> pagesData = _viewer.getPages(fileCredentials, pageNumbers);

            List<PageDescription> pages = new ArrayList<>();
            final String searchTerm = _searchTermResolver.resolveSearchTerm(guid);
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

            LoadDocumentDescriptionResponse result = new LoadDocumentDescriptionResponse(guid,
                    documentDescription.getFileType(), documentDescription.isPrintAllowed(), pages, searchTerm);

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

    public ViewerActionResult loadDocumentPageResource(LoadDocumentPageResourceRequest request) {
        if (!_config.isHtmlMode()) {
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

            return new ViewerActionResult(contentType, HttpURLConnection.HTTP_OK, bytes);
        } catch (Exception e) {
            LOGGER.error("Exception throws while loading document page resource: guid={}", guid, e);
            return errorJsonResult(e.getMessage());
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
        } catch (Exception e) {
            LOGGER.error("Exception throws while downloading document: path={}", path, e);
            return errorJsonResult(e.getMessage());
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
        } catch (Exception e) {
            LOGGER.error("Exception throws while uploading document: fileNameOrUrl={}", fileNameOrUrl, e);
            return errorJsonResult(e.getMessage());
        }
    }

    public ViewerActionResult printPdf(PrintPdfRequest request) {
        if (!_config.isPrint()) {
            return errorJsonResult("Printing files is disabled.");
        }

        final String guid = request.getGuid();
        try {
            FileCredentials fileCredentials = new FileCredentials(guid, request.getFileType(),
                    request.getPassword());

            String fileName = _fileNameResolver.resolveFileName(guid);
            String pdfFileName = StringExtensions.changeExtension(fileName, ".pdf");
            byte[] pdfFileBytes = _viewer.getPdf(fileCredentials);

            return new ViewerActionResult("application/pdf", HttpURLConnection.HTTP_OK, new FileResponse(pdfFileBytes, pdfFileName));
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase(Locale.ROOT).contains("password")) {
                String message = request.getPassword() == null || request.getPassword().isEmpty() ? "Password Required" : "Incorrect Password";

                return forbiddenJsonResult(message);
            }
            LOGGER.error("Exception throws while printing pdf: guid={}", guid, e);
            return errorJsonResult(e.getMessage());
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
        final String guid = request.getGuid();
        try {
            FileCredentials fileCredentials = new FileCredentials(
                    guid, request.getFileType(), request.getPassword());
            List<Page> pages = _viewer.getPages(fileCredentials, request.getPages());
            List<PageContent> pageContents = pages.stream()
                    .map(page -> new PageContent(page.getPageNumber(), page.getContent()))
                    .collect(Collectors.toList());

            return okJsonResult(pageContents);
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

    @Override
    public void close() {
        if (this._viewer != null) {
            this._viewer.close();
        }
    }
}
