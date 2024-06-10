package com.groupdocs.viewerui.handler;

import com.groupdocs.viewerui.Keys;
import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.function.HeaderAdder;
import com.groupdocs.viewerui.ui.api.UiConfigProvider;
import com.groupdocs.viewerui.ui.api.UiConfigProviderFactory;
import com.groupdocs.viewerui.ui.api.awss3.AwsS3Options;
import com.groupdocs.viewerui.ui.api.awss3.cache.AwsS3FileCache;
import com.groupdocs.viewerui.ui.api.awss3.storage.AwsS3FileStorage;
import com.groupdocs.viewerui.ui.api.cache.FileCacheFactory;
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
import com.groupdocs.viewerui.ui.core.cache.internal.MemoryCache;
import com.groupdocs.viewerui.ui.core.cache.internal.MemoryCacheFactory;
import com.groupdocs.viewerui.ui.core.cache.local.LocalFileCache;
import com.groupdocs.viewerui.ui.core.cache.local.config.LocalCacheConfig;
import com.groupdocs.viewerui.ui.core.cache.memory.InMemoryFileCache;
import com.groupdocs.viewerui.ui.core.cache.memory.config.InMemoryCacheConfig;
import com.groupdocs.viewerui.ui.core.configuration.Config;
import com.groupdocs.viewerui.ui.core.entities.ConfigEntry;
import com.groupdocs.viewerui.ui.core.extensions.StringExtensions;
import com.groupdocs.viewerui.ui.core.extensions.UrlExtensions;
import com.groupdocs.viewerui.ui.core.serialize.ISerializer;
import com.groupdocs.viewerui.ui.core.serialize.JacksonJsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * Endpoint handler for specific cases when neither Servlets not Jakarta API are not used in an application.
 */
public class CommonViewerEndpointHandler {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonViewerEndpointHandler.class);
    private UiOptions _uiOptions = new UiOptions();

    private ApiOptions _apiOptions = new ApiOptions();

    private ViewerConfig _viewerConfig = new ViewerConfig();

    private Config _config;

    private IUiResourcesReader _uiResourcesReader;

    private IUiResourcesModifier _uiResourcesModifier;

    private ConfigEntryFactory _configEntryFactory;
    private IActionNameDetector _requestDetector;

    private ISerializer _serializer;
    private IFileStorage _fileStorage;
    private ViewerFactory _viewerFactory;
    private ViewerControllerFactory _viewerControllerFactory;

    protected CommonViewerEndpointHandler() {
    }

    /**
     * Sets up the GroupDocs.Viewer for Java UI in common.
     *
     * @param configConsumer a consumer that accepts a ViewerConfig and a Config object to apply the configuration settings.
     * @return a reference to `this` object.
     */
    public static CommonViewerEndpointHandler setupGroupDocsViewer(BiConsumer<ViewerConfig, Config> configConsumer) {
        final CommonViewerEndpointHandler commonViewerEndpointHandler = new CommonViewerEndpointHandler();
        return setupGroupDocsViewer(commonViewerEndpointHandler, configConsumer);
    }

    public static <T extends CommonViewerEndpointHandler> T setupGroupDocsViewer(T viewerEndpointHandler, BiConsumer<ViewerConfig, Config> configConsumer) {
        final ViewerConfig viewerConfig = viewerEndpointHandler.getViewerConfig();
        final Config config = viewerEndpointHandler.getConfig();
        configConsumer.accept(viewerConfig, config);
        // TODO: Some checks
        LOGGER.info("GroupDocs Viewer has been set up.");
        LOGGER.debug("Viewer config: {}, \nConfig: {}", viewerConfig, config);
        return viewerEndpointHandler;
    }

    /**
     * Sets up the GroupDocs.Viewer UI by configuring UI specific options.
     *
     * @param optionsConsumer a Consumer that accepts a UiOptions object to apply the configuration settings.
     * @return a reference to `this` object.
     * @throws IllegalArgumentException if the path to the UI is null or does not start with '/' character.
     */
    public CommonViewerEndpointHandler setupGroupDocsViewerUI(Consumer<UiOptions> optionsConsumer) {
        final UiOptions uiOptions = getUiOptions();
        optionsConsumer.accept(uiOptions);

        final String uiPath = uiOptions.getUiPath();
        if (uiPath == null || !uiPath.startsWith("/")) {
            throw new IllegalArgumentException(
                    "The value for customized path can't be null and need to start with / character.");
        }
        LOGGER.info("GroupDocs Viewer UI has been set up.");
        LOGGER.debug("Ui options: {}", uiOptions);
        return this;
    }

    /**
     * Sets up the GroupDocs.Viewer API by configuring API specific options.
     *
     * @param optionsConsumer a Consumer that accepts an ApiOptions object to apply the configuration settings.
     * @return a reference to `this` object.
     */
    public CommonViewerEndpointHandler setupGroupDocsViewerApi(Consumer<ApiOptions> optionsConsumer) {
        setupGroupDocsViewerApi(optionsConsumer, new DefaultViewerFactory(), new DefaultViewerControllerFactory());
        return this;
    }

    public CommonViewerEndpointHandler setupGroupDocsViewerApi(Consumer<ApiOptions> optionsConsumer, ViewerFactory viewerFactory, ViewerControllerFactory viewerControllerFactory) {
        final ApiOptions apiOptions = getApiOptions();
        optionsConsumer.accept(apiOptions);

        final String apiEndpoint = apiOptions.getApiEndpoint();
        if (apiEndpoint == null || apiEndpoint.isEmpty()) {
            throw new IllegalArgumentException("The value can't be null or empty.");
        }

        final String apiPath = apiOptions.getApiEndpoint();
        if (apiPath == null || !apiPath.startsWith("/")) {
            throw new IllegalArgumentException("The value for customized path can't be null and need to start with / character.");
        }
        this._viewerFactory = viewerFactory;
        this._viewerControllerFactory = viewerControllerFactory;

        LOGGER.info("GroupDocs Viewer API has been set up.");
        LOGGER.debug("Api options: {}", apiOptions);
        return this;
    }

    /**
     * Sets up the local storage for GroupDocs.Viewer by specifying the storage path.
     *
     * @param storagePath the path where the files will be stored locally.
     * @return a reference to `this` object.
     */
    public CommonViewerEndpointHandler setupLocalStorage(Path storagePath) {
        final Path absoluteLocalStoragePath = storagePath.toAbsolutePath();
        _fileStorage = new LocalFileStorage(absoluteLocalStoragePath);
        LOGGER.info("GroupDocs Viewer local storage has been set up.");
        LOGGER.debug("Local storage path: {}", absoluteLocalStoragePath);
        return this;
    }

    /**
     * Sets up the AWS S3 storage for GroupDocs.Viewer by using a consumer that accepts an AwsS3Options object to apply the storage settings.
     *
     * @param storageConfigConsumer a consumer function that configures the AWS S3 storage using a AwsS3Options object.
     * @return a reference to `this` object.
     */
    public CommonViewerEndpointHandler setupAwsS3Storage(Consumer<AwsS3Options> storageConfigConsumer) {
        final AwsS3Options awsS3Options = new AwsS3Options();
        storageConfigConsumer.accept(awsS3Options);
        _fileStorage = new AwsS3FileStorage(awsS3Options);
        LOGGER.info("GroupDocs Viewer AWS S3 storage has been set up.");
        LOGGER.debug("AWS S3 storage options: {}", awsS3Options);
        return this;
    }

    /**
     * Sets up the local cache for GroupDocs.Viewer by using a consumer that accepts an LocalCacheConfig object to apply the cache settings.
     *
     * @param cacheConfigConsumer a consumer function that configures the cache using a LocalCacheConfig object.
     * @return a reference to `this` object.
     */
    public CommonViewerEndpointHandler setupLocalCache(Consumer<LocalCacheConfig> cacheConfigConsumer) {
        final LocalCacheConfig cacheConfig = new LocalCacheConfig();
        cacheConfigConsumer.accept(cacheConfig);
        final boolean supplierPresent = FileCacheFactory.isSupplierPresent();
        FileCacheFactory.setSupplier(() -> {
            final ISerializer serializer = getSerializer();
            return new LocalFileCache(serializer, cacheConfig);
        });
        LOGGER.info("GroupDocs Viewer local cache has been set up.");
        LOGGER.debug("Cache config: {}, is previous cache setup replaced: {}", cacheConfig, supplierPresent);
        return this;
    }

    /**
     * Sets up the in-memory cache for GroupDocs.Viewer by using a consumer that accepts an InMemoryCacheConfig object to apply the cache settings.
     *
     * @param cacheConfigConsumer a consumer function that configures the cache using an InMemoryCacheConfig object.
     * @return a reference to `this` object.
     */
    public CommonViewerEndpointHandler setupInMemoryCache(Consumer<InMemoryCacheConfig> cacheConfigConsumer) {
        final InMemoryCacheConfig cacheConfig = new InMemoryCacheConfig();
        cacheConfigConsumer.accept(cacheConfig);
        final boolean supplierPresent = FileCacheFactory.isSupplierPresent();
        FileCacheFactory.setSupplier(() -> {
            final MemoryCache memoryCache = MemoryCacheFactory.getInstance();
            return new InMemoryFileCache(memoryCache, cacheConfig);
        });
        LOGGER.info("GroupDocs Viewer im-memory cache has been set up.");
        LOGGER.debug("Cache config: {}, is previous cache setup replaced: {}", cacheConfig, supplierPresent);
        return this;
    }

    /**
     * Sets up the AWS S3 cache for GroupDocs.Viewer by using a consumer that accepts an {@link AwsS3Options} object to apply the cache settings.
     *
     * @param cacheConfigConsumer a consumer function that configures the cache using a {@link AwsS3Options} object.
     * @return a reference to `this` object.
     */
    public CommonViewerEndpointHandler setupAwsS3Cache(Consumer<AwsS3Options> cacheConfigConsumer) {
        final AwsS3Options cacheConfig = new AwsS3Options();
        cacheConfigConsumer.accept(cacheConfig);
        final boolean supplierPresent = FileCacheFactory.isSupplierPresent();
        FileCacheFactory.setSupplier(() -> {
            final ISerializer serializer = getSerializer();
            return new AwsS3FileCache(cacheConfig, serializer);
        });
        LOGGER.info("GroupDocs Viewer AWS S3 cache has been set up.");
        LOGGER.debug("Cache config: {}, is previous cache setup replaced: {}", cacheConfig, supplierPresent);
        return this;
    }

    /**
     * Handles a viewer request by determining the action to be performed based on the request URL and executing the corresponding handler.
     *
     * @param requestUrl      the URL of the viewer request.
     * @param queryString     the query string of the viewer request.
     * @param requestStream   the input stream of the viewer request.
     * @param headerAdder     the object which will be used to add response headers.
     * @param responseStream  the output stream for the viewer response.
     * @return the HTTP status code indicating the result of the viewer request.
     * @throws ViewerUiException if an error occurs while handling the viewer request.
     */
    public int handleViewerRequest(String requestUrl, String queryString, InputStream requestStream, HeaderAdder headerAdder, OutputStream responseStream) {
        LOGGER.info("Handling Viewer request: {}", requestUrl);
        LOGGER.debug("Request url: {}, query string: {}", requestUrl, queryString);
        try {
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
                    return handleApiRequest(actionName, queryString, requestStream, headerAdder, responseStream);
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
        return viewerFactory.createViewer(viewerConfig, apiOptions, () -> _fileStorage); // it
    }

    /**
     * Handles a viewer upload request by uploading a document and returning the result.
     *
     * @param submittedFileStream    the input stream containing the submitted file.
     * @param submittedFileName      the name of the submitted file.
     * @param isRewrite              a flag indicating whether the file should be overwritten if it already exists.
     * @param headerAdder            the object which will be used to add response headers.
     * @param responseStream         the output stream for the viewer response.
     * @return the HTTP status code indicating the result of the viewer upload request.
     * @throws ViewerUiException     if an error occurs while handling the viewer upload request.
     */
    public int handleViewerUploadRequest(InputStream submittedFileStream, String submittedFileName, boolean isRewrite, HeaderAdder headerAdder, OutputStream responseStream) {
        final ViewerControllerFactory viewerControllerFactory = getViewerControllerFactory();
        final IViewer viewer = createViewer();
        final Config config = getConfig();
        final ISerializer serializer = getSerializer();
        LOGGER.info("Handling Viewer upload request.");
        LOGGER.debug("Submitted file name: {}, is rewrite: {}", submittedFileName, isRewrite);
        // will be disposed by viewerController
        try (ViewerController viewerController = viewerControllerFactory.createViewerController(config, viewer, () -> _fileStorage)) {
            final ViewerActionResult uploadDocumentResult = viewerController.uploadDocument(submittedFileName, submittedFileStream, isRewrite);
            headerAdder.addHeader(CONTENT_TYPE, uploadDocumentResult.getContentType());
            serializer.serialize(uploadDocumentResult.getValue(), responseStream);
            return uploadDocumentResult.getStatusCode();
        }
    }

    private int handleUiRequest(String requestUrl, HeaderAdder headerAdder, OutputStream responseStream) throws IOException {

        final UiResource uiResource = prepareUiResourceForResponse(requestUrl);

        headerAdder.addHeader(CONTENT_TYPE, uiResource.getContentType());

        final String content = uiResource.getContent();
        responseStream.write(content.getBytes(StandardCharsets.UTF_8));
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

    private int handleApiRequest(ActionName actionName, String queryString, InputStream requestStream, HeaderAdder headerAdder, OutputStream responseStream) {
        if (actionName == ActionName.UI_RESOURCE || actionName == ActionName.LOAD_CONFIG) {
            // They have already handled
            return HttpURLConnection.HTTP_INTERNAL_ERROR;
        }

        final ViewerControllerFactory viewerControllerFactory = getViewerControllerFactory();
        // will be disposed by viewerController
        final IViewer viewer = createViewer();
        final Config config = getConfig();
        final ISerializer serializer = getSerializer();
        try (ViewerController viewerController = viewerControllerFactory.createViewerController(config, viewer, () -> _fileStorage)) {
            switch (actionName) {
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
                                LOGGER.error("Exception throws while writing document page data to response stream: actionName={} queryString={}", actionName, queryString, e);
                                throw new ViewerUiException(e);
                            }
                        } else {
                            LOGGER.warn("Unexpected type of response object: {}", documentPageResourceResultValue);
                            return HttpURLConnection.HTTP_INTERNAL_ERROR;
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
                                LOGGER.error("Exception throws while writing file data to response stream: actionName={} queryString={}", actionName, queryString, e);
                                throw new ViewerUiException(e);
                            }
                        } else {
                            LOGGER.warn("Unexpected type of response object: {}", downloadDocumentResultValue);
                            return HttpURLConnection.HTTP_INTERNAL_ERROR;
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
                                LOGGER.error("Exception throws while writing pdf print data to response stream: actionName={} queryString={}", actionName, queryString, e);
                                throw new ViewerUiException(e);
                            }
                        } else {
                            LOGGER.warn("Unexpected type of response object: {}", printPdfResultValue);
                            return HttpURLConnection.HTTP_INTERNAL_ERROR;
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

    public IFileStorage getFileStorage() {
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

    /**
     * Checks if the given request URL is an upload request so that you will know which handler should be used ({@link #handleViewerRequest(String, String, InputStream, HeaderAdder, OutputStream)} or {@link #handleViewerUploadRequest(InputStream, String, boolean, HeaderAdder, OutputStream)}).
     *
     * @param requestUrl the URL of the request to be checked.
     * @return true if the request is an upload request, false otherwise.
     */
    public boolean isUploadRequest(String requestUrl) {
        final IActionNameDetector requestDetector = getRequestDetector();
        final ActionName actionName = requestDetector.detectActionName(requestUrl);
        return actionName == ActionName.API_UPLOAD_DOCUMENT;
    }
}
