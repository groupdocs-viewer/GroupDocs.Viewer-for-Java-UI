package com.groupdocs.viewerui.mapper;

import com.groupdocs.viewerui.Keys;
import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.function.HeaderAdder;
import com.groupdocs.viewerui.ui.api.UiConfigProvider;
import com.groupdocs.viewerui.ui.api.UiConfigProviderFactory;
import com.groupdocs.viewerui.ui.api.controller.ViewerController;
import com.groupdocs.viewerui.ui.api.factory.DefaultViewerControllerFactory;
import com.groupdocs.viewerui.ui.api.factory.DefaultViewerFactory;
import com.groupdocs.viewerui.ui.api.factory.ViewerControllerFactory;
import com.groupdocs.viewerui.ui.api.factory.ViewerFactory;
import com.groupdocs.viewerui.ui.api.infrastructure.ViewerActionResult;
import com.groupdocs.viewerui.ui.api.local.storage.LocalFileStorage;
import com.groupdocs.viewerui.ui.api.models.*;
import com.groupdocs.viewerui.ui.configuration.ApiOptions;
import com.groupdocs.viewerui.ui.configuration.UiOptions;
import com.groupdocs.viewerui.ui.configuration.ViewerConfig;
import com.groupdocs.viewerui.ui.core.*;
import com.groupdocs.viewerui.ui.core.configuration.Config;
import com.groupdocs.viewerui.ui.core.entities.ConfigEntry;
import com.groupdocs.viewerui.ui.core.extensions.StringExtensions;
import com.groupdocs.viewerui.ui.core.extensions.UrlExtensions;
import com.groupdocs.viewerui.ui.core.serialize.ISerializer;
import com.groupdocs.viewerui.ui.core.serialize.JacksonJsonSerializer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class CommonViewerEndpointHandler {
    public static final String CONTENT_TYPE = "Content-Type";
    private UiOptions _uiOptions = new UiOptions();

    private ApiOptions _apiOptions = new ApiOptions();

    private ViewerConfig _viewerConfig = new ViewerConfig();

    private Config _config;

    private IUiResourcesReader _uiResourcesReader;

    private IUiResourcesModifier _uiResourcesModifier;

    private ConfigEntryFactory _configEntryFactory;
    private IActionNameDetector _requestDetector;

    private ISerializer _serializer;
    private LocalFileStorage _fileStorage;
    private ViewerFactory _viewerFactory;
    private ViewerControllerFactory _viewerControllerFactory;

    private CommonViewerEndpointHandler() {
    }

    public static CommonViewerEndpointHandler setupGroupDocsViewer(BiConsumer<ViewerConfig, Config> configConsumer) {
        final CommonViewerEndpointHandler commonViewerEndpointHandler = new CommonViewerEndpointHandler();
        final ViewerConfig viewerConfig = commonViewerEndpointHandler.getViewerConfig();
        final Config config = commonViewerEndpointHandler.getConfig();
        configConsumer.accept(viewerConfig, config);
        // TODO: Some checks
        return commonViewerEndpointHandler;
    }

    public CommonViewerEndpointHandler setupGroupDocsViewerUI(Consumer<UiOptions> optionsConsumer) {
        optionsConsumer.accept(this._uiOptions);

        final String uiPath = this._uiOptions.getUiPath();
        if (uiPath == null || !uiPath.startsWith("/")) {
            throw new IllegalArgumentException(
                    "The value for customized path can't be null and need to start with / character.");
        }
        return this;
    }

    public CommonViewerEndpointHandler setupGroupDocsViewerApi(Consumer<ApiOptions> optionsConsumer) {
        setupGroupDocsViewerApi(optionsConsumer, new DefaultViewerFactory(), new DefaultViewerControllerFactory());
        return this;
    }

    public CommonViewerEndpointHandler setupGroupDocsViewerApi(Consumer<ApiOptions> optionsConsumer, ViewerFactory viewerFactory, ViewerControllerFactory viewerControllerFactory) {
        optionsConsumer.accept(this._apiOptions);

        final String apiEndpoint = this._apiOptions.getApiEndpoint();
        if (apiEndpoint == null || apiEndpoint.isEmpty()) {
            throw new IllegalArgumentException("The value can't be null or empty.");
        }

        final String apiPath = this._apiOptions.getApiEndpoint();
        if (apiPath == null || !apiPath.startsWith("/")) {
            throw new IllegalArgumentException(
                    "The value for customized path can't be null and need to start with / character.");
        }
        this._viewerFactory = viewerFactory;
        this._viewerControllerFactory = viewerControllerFactory;
        return this;
    }

    public CommonViewerEndpointHandler setupLocalStorage(Path storagePath) {
        _fileStorage = new LocalFileStorage(storagePath);
        return this;
    }

    public int handleViewerRequest(String requestUrl, String queryString, InputStream requestStream, HeaderAdder headerAdder, OutputStream responseStream) {
        final ViewerControllerFactory viewerControllerFactory = getViewerControllerFactory();
        final IViewer viewer = createViewer();
        final Config config = getConfig();
        // will be disposed by viewerController
        try (ViewerController viewerController = viewerControllerFactory.createViewerController(config, viewer, () -> _fileStorage)) {
            final IActionNameDetector requestDetector = getRequestDetector();
            final ActionName actionName = requestDetector.detectActionName(requestUrl);
            if (actionName == null) {
                throw new ViewerUiException("actionName was not detected correctly, url: " + requestUrl);
            }
            switch (actionName) {
                case UI_RESOURCE:
                    return handleUiRequest(requestUrl, headerAdder, responseStream);
                case LOAD_CONFIG:
                    return handleConfigRequest(headerAdder, responseStream);
                default:
                    return handleApiRequest(viewerController, actionName, queryString, requestStream, headerAdder, responseStream);
            }
        } catch (ViewerUiException e) {
            throw e;
        } catch (Exception e) {
            throw new ViewerUiException("Exception was thrown while handling request", e);
        }
    }

    private IViewer createViewer() {
        final ViewerFactory viewerFactory = getViewerFactory();
        final ViewerConfig viewerConfig = getViewerConfig();
        final ApiOptions apiOptions = getApiOptions();
        final IViewer viewer = viewerFactory.createViewer(viewerConfig, apiOptions, () -> _fileStorage); // it
        return viewer;
    }

    public int handleViewerUploadRequest(InputStream submittedFileStream, String submittedFileName, boolean isRewrite, HeaderAdder headerAdder, OutputStream responseStream) {
        final ViewerControllerFactory viewerControllerFactory = getViewerControllerFactory();
        final IViewer viewer = createViewer();
        final Config config = getConfig();
        final ISerializer serializer = getSerializer();
        // will be disposed by viewerController
        try (ViewerController viewerController = viewerControllerFactory.createViewerController(config, viewer, () -> _fileStorage)) {
            final ViewerActionResult uploadDocumentResult = viewerController.uploadDocument(submittedFileName, submittedFileStream, isRewrite);
            headerAdder.addHeader(CONTENT_TYPE, uploadDocumentResult.getContentType());
            serializer.serialize(uploadDocumentResult.getValue(), responseStream);
            return uploadDocumentResult.getStatusCode();
        }
    }

    private int handleUiRequest(String requestUrl, HeaderAdder headerAdder, OutputStream responseStream)
            throws IOException {
        final UiResource uiResource = prepareUiResourceForResponse(requestUrl);

        headerAdder.addHeader(CONTENT_TYPE, uiResource.getContentType());

        final String content = uiResource.getContent();
        IOUtils.write(content, responseStream, StandardCharsets.UTF_8);
        responseStream.flush();

        return HttpURLConnection.HTTP_OK;
    }

    private int handleConfigRequest(HeaderAdder headerAdder, OutputStream responseStream) throws IOException {
        final ConfigEntryFactory configEntryFactory = getConfigEntryFactory();
        final Config config = getConfig();
        final ViewerConfig viewerConfig = getViewerConfig();

        headerAdder.addHeader(CONTENT_TYPE, "application/json");
        final UiConfigProvider configProvider = UiConfigProviderFactory.getInstance();
        config.setViewerType(viewerConfig.getViewerType());
        configProvider.configureUI(config);
        final ConfigEntry configEntry = configEntryFactory.createConfigEntry(config);
        final ISerializer serializer = getSerializer();
        serializer.serialize(configEntry, responseStream);
        responseStream.flush();

        return HttpURLConnection.HTTP_OK;
    }

    private int handleApiRequest(ViewerController viewerController, ActionName actionName, String queryString, InputStream requestStream, HeaderAdder headerAdder, OutputStream responseStream) {
        final ISerializer serializer = getSerializer();
        switch (actionName) {
            case UI_RESOURCE:
                break;
            case LOAD_CONFIG:
                break;
            case API_LOAD_FILE_TREE:
                LoadFileTreeRequest fileTreeRequest = serializer.deserialize(requestStream, LoadFileTreeRequest.class);
                final ViewerActionResult fileTreeResult = viewerController.loadFileTree(fileTreeRequest);
                headerAdder.addHeader(CONTENT_TYPE, fileTreeResult.getContentType());
                serializer.serialize(fileTreeResult.getValue(), responseStream);
                return fileTreeResult.getStatusCode();
            case API_LOAD_DOCUMENT_DESCRIPTION:
                LoadDocumentDescriptionRequest documentDescriptionRequest = serializer.deserialize(requestStream, LoadDocumentDescriptionRequest.class);
                final ViewerActionResult documentDescriptionResult = viewerController.loadDocumentDescription(documentDescriptionRequest);
                headerAdder.addHeader(CONTENT_TYPE, documentDescriptionResult.getContentType());
                serializer.serialize(documentDescriptionResult.getValue(), responseStream);
                return documentDescriptionResult.getStatusCode();
            case API_LOAD_DOCUMENT_PAGE_RESOURCE:
                LoadDocumentPageResourceRequest documentPageResourceRequest = createFromQueryString(queryString);
                final ViewerActionResult documentPageResourceResult = viewerController.loadDocumentPageResource(documentPageResourceRequest);
                headerAdder.addHeader(CONTENT_TYPE, documentPageResourceResult.getContentType());
                final int documentPageResourceStatusCode = documentPageResourceResult.getStatusCode();
                if (documentPageResourceStatusCode == HttpURLConnection.HTTP_OK) {
                    final Object documentPageResourceResultValue = documentPageResourceResult.getValue();
                    if (documentPageResourceResultValue instanceof byte[]) {
                        try {
                            responseStream.write((byte[]) documentPageResourceResultValue);
                        } catch (IOException e) {
                            e.printStackTrace(); // TODO: Add logging
                            throw new RuntimeException(e);
                        }
                    } else {
                        // TODO: Log warning
                    }
                } else {
                    serializer.serialize(documentPageResourceResult.getValue(), responseStream);
                }
                return documentPageResourceResult.getStatusCode();
            case API_DOWNLOAD_DOCUMENT:
                final Map<String, String> queryParams = UrlExtensions.extractParams(queryString);
                final String filePath = queryParams.get("path");
                final ViewerActionResult downloadDocumentResult = viewerController.downloadDocument(filePath);
                headerAdder.addHeader(CONTENT_TYPE, downloadDocumentResult.getContentType());
                final int downloadDocumentStatusCode = downloadDocumentResult.getStatusCode();
                if (downloadDocumentStatusCode == HttpURLConnection.HTTP_OK) {
                    final Object downloadDocumentResultValue = downloadDocumentResult.getValue();
                    if (downloadDocumentResultValue instanceof FileResponse) {
                        final FileResponse fileResponse = (FileResponse) downloadDocumentResultValue;
                        headerAdder.addHeader("Content-Length", Long.toString(fileResponse.data.length));
                        headerAdder.addHeader("Content-Disposition", "attachment; filename=\"" + fileResponse.fileName + "\"; filename*=UTF-8''" + fileResponse.fileName);
                        try {
                            responseStream.write(fileResponse.data);
                        } catch (IOException e) {
                            e.printStackTrace(); // TODO: Add logging
                            throw new RuntimeException(e);
                        }
                    } else {
                        // TODO: Log warning
                    }
                } else {
                    serializer.serialize(downloadDocumentResult.getValue(), responseStream);
                }
                return downloadDocumentStatusCode;
            case API_LOAD_DOCUMENT_PAGES:
                LoadDocumentPagesRequest documentPagesRequest = serializer.deserialize(requestStream, LoadDocumentPagesRequest.class);
                final ViewerActionResult documentPagesResult = viewerController.loadDocumentPages(documentPagesRequest);
                headerAdder.addHeader(CONTENT_TYPE, documentPagesResult.getContentType());
                serializer.serialize(documentPagesResult.getValue(), responseStream);
                return documentPagesResult.getStatusCode();
            case API_PRINT_PDF:
                PrintPdfRequest printPdfRequest = serializer.deserialize(requestStream, PrintPdfRequest.class);
                final ViewerActionResult printPdfResult = viewerController.printPdf(printPdfRequest);
                headerAdder.addHeader(CONTENT_TYPE, printPdfResult.getContentType());
                final int printPdfStatusCode = printPdfResult.getStatusCode();
                if (printPdfStatusCode == HttpURLConnection.HTTP_OK) {
                    final Object printPdfResultValue = printPdfResult.getValue();
                    if (printPdfResultValue instanceof FileResponse) {
                        final FileResponse fileResponse = (FileResponse) printPdfResultValue;
                        headerAdder.addHeader("Content-Length", Long.toString(fileResponse.data.length));
                        headerAdder.addHeader("Content-Disposition", "attachment; filename=\"" + fileResponse.fileName + "\"; filename*=UTF-8''" + fileResponse.fileName);
                        try {
                            responseStream.write(fileResponse.data);
                        } catch (IOException e) {
                            e.printStackTrace(); // TODO: Add logging
                            throw new RuntimeException(e);
                        }
                    } else {
                        // TODO: Log warning
                    }
                } else {
                    serializer.serialize(printPdfResult.getValue(), responseStream);
                }
                return printPdfStatusCode;
//            case API_UPLOAD_DOCUMENT: // handleViewerUploadRequest

//            case API_LOAD_DOCUMENT_PAGE:
//                break;
//            case API_LOAD_THUMBNAILS:
//                break;
            default:
        }
        return HttpURLConnection.HTTP_NOT_FOUND;
    }

    private LoadDocumentPageResourceRequest createFromQueryString(String queryString) {
        final Map<String, String> queryParams = UrlExtensions.extractParams(queryString);
        final LoadDocumentPageResourceRequest documentPageResource = new LoadDocumentPageResourceRequest();
        documentPageResource.setGuid(queryParams.get("guid"));
        documentPageResource.setPassword(queryParams.get("password"));
        documentPageResource.setFileType(queryParams.get("fileType"));
        documentPageResource.setPageNumber(Integer.parseInt(queryParams.getOrDefault("pageNumber", "0")));
        documentPageResource.setResourceName(queryParams.get("resourceName"));
        return documentPageResource;
    }

    private UiResource prepareUiResourceForResponse(String requestUrl) throws IOException {
        String fileName;
        final UiOptions uiOptions = getUiOptions();
        final String uiPath = uiOptions.getUiPath();
        if (uiPath.equals(StringExtensions.trimTrailingSlash(requestUrl))) {
            fileName = Keys.GROUPDOCSVIEWERUI_MAIN_UI_RESOURCE;
        } else {
            try {
                final URL url = new URL(requestUrl);
                fileName = url.getFile();
            } catch (java.net.MalformedURLException e) {
                // requestUrl is not absolute or not parseable for URL
                fileName = Paths.get(requestUrl).getFileName().toString();
            }
        }

        int lastIndex = fileName.lastIndexOf('/');
        if (lastIndex != -1) {
            fileName = fileName.substring(lastIndex + 1);
        }

        final IUiResourcesReader uiResourcesReader = getUiResourcesReader();
        final UiResource uiResource = uiResourcesReader.getUiResource(fileName);
        // TODO: check uiResource
        final IUiResourcesModifier uiResourcesModifier = getUiResourcesModifier();
        final Config config = getConfig();
        final String baseUrl = config.getBaseUrl();
        uiResourcesModifier.modifyResource(uiResource, baseUrl);
        return uiResource;
    }

    public Config getConfig() {
        if (this._config == null) {
            _config = new Config();
        }
        return _config;
    }

    public void setConfig(Config config) {
        this._config = config;
    }

    public UiOptions getUiOptions() {
        return _uiOptions;
    }

    public void setUiOptions(UiOptions uiOptions) {
        this._uiOptions = uiOptions;
    }

    public ApiOptions getApiOptions() {
        return _apiOptions;
    }

    public void setApiOptions(ApiOptions apiOptions) {
        this._apiOptions = apiOptions;
    }

    public IUiResourcesReader getUiResourcesReader() {
        if (this._uiResourcesReader == null) {
            this._uiResourcesReader = new UiEmbeddedResourcesReader();
        }
        return _uiResourcesReader;
    }

    public void setUiResourcesReader(IUiResourcesReader uiResourcesReader) {
        this._uiResourcesReader = uiResourcesReader;
    }

    public IUiResourcesModifier getUiResourcesModifier() {
        if (this._uiResourcesModifier == null) {
            this._uiResourcesModifier = new DefaultUiResourcesModifier(_uiOptions, _apiOptions);
        }
        return _uiResourcesModifier;
    }

    public void setUiResourcesModifier(IUiResourcesModifier uiResourcesModifier) {
        this._uiResourcesModifier = uiResourcesModifier;
    }

    public IActionNameDetector getRequestDetector() {
        if (this._requestDetector == null) {
            final UiOptions uiOptions = getUiOptions();
            final ApiOptions apiOptions = getApiOptions();
            this._requestDetector = new DefaultActionNameDetector(uiOptions, apiOptions);
        }
        return _requestDetector;
    }

    public void setRequestDetector(IActionNameDetector requestDetector) {
        this._requestDetector = requestDetector;
    }

    public ConfigEntryFactory getConfigEntryFactory() {
        if (_configEntryFactory == null) {
            _configEntryFactory = new ConfigEntryFactory();
        }
        return _configEntryFactory;
    }

    public void setConfigEntryFactory(ConfigEntryFactory configEntryFactory) {
        this._configEntryFactory = configEntryFactory;
    }

    public ISerializer getSerializer() {
        if (this._serializer == null) {
            this._serializer = new JacksonJsonSerializer();
        }
        return _serializer;
    }

    public void setSerializer(ISerializer serializer) {
        this._serializer = serializer;
    }

    public void setConfigProvider(UiConfigProvider configProvider) {
        UiConfigProviderFactory.setInstance(configProvider);
    }

    public ViewerConfig getViewerConfig() {
        return _viewerConfig;
    }

    public void setViewerConfig(ViewerConfig viewerConfig) {
        this._viewerConfig = viewerConfig;
    }

    public LocalFileStorage getFileStorage() {
        return _fileStorage;
    }

    public void setFileStorage(LocalFileStorage fileStorage) {
        this._fileStorage = fileStorage;
    }

    public ViewerFactory getViewerFactory() {
        return _viewerFactory;
    }

    public void setViewerFactory(ViewerFactory viewerFactory) {
        this._viewerFactory = viewerFactory;
    }

    public ViewerControllerFactory getViewerControllerFactory() {
        return _viewerControllerFactory;
    }

    public void setViewerControllerFactory(ViewerControllerFactory viewerControllerFactory) {
        this._viewerControllerFactory = viewerControllerFactory;
    }

    public boolean isUploadRequest(String requestUrl) {
        final IActionNameDetector requestDetector = getRequestDetector();
        final ActionName actionName = requestDetector.detectActionName(requestUrl);
        return actionName == ActionName.API_UPLOAD_DOCUMENT;
    }
}
