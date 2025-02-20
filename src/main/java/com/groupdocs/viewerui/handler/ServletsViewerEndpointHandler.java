package com.groupdocs.viewerui.handler;

import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.api.awss3.AwsS3Options;
import com.groupdocs.viewerui.ui.api.azure.AzureBlobOptions;
import com.groupdocs.viewerui.ui.api.factory.ViewerControllerFactory;
import com.groupdocs.viewerui.ui.api.factory.ViewerFactory;
import com.groupdocs.viewerui.ui.configuration.ApiOptions;
import com.groupdocs.viewerui.ui.configuration.UiOptions;
import com.groupdocs.viewerui.ui.configuration.ViewerConfig;
import com.groupdocs.viewerui.ui.core.cache.local.config.LocalCacheConfig;
import com.groupdocs.viewerui.ui.core.cache.memory.config.InMemoryCacheConfig;
import com.groupdocs.viewerui.ui.core.configuration.Config;
import com.groupdocs.viewerui.ui.core.extensions.StreamExtensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Endpoint handler for web applications which use Servlets API
 */
public class ServletsViewerEndpointHandler extends CommonViewerEndpointHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServletsViewerEndpointHandler.class);

    protected ServletsViewerEndpointHandler() {
    }


    /**
     * Sets up the GroupDocs.Viewer for Java UI in common.
     *
     * @param configConsumer a consumer that accepts a ViewerConfig and a Config object to apply the configuration settings.
     * @return a reference to `this` object.
     */
    public static ServletsViewerEndpointHandler setupGroupDocsViewer(BiConsumer<ViewerConfig, Config> configConsumer) {
        return CommonViewerEndpointHandler.setupGroupDocsViewer(new ServletsViewerEndpointHandler(), configConsumer);
    }

    @Override
    public ServletsViewerEndpointHandler setupGroupDocsViewerUI(Consumer<UiOptions> optionsConsumer) {
        return (ServletsViewerEndpointHandler) super.setupGroupDocsViewerUI(optionsConsumer);
    }

    @Override
    public ServletsViewerEndpointHandler setupGroupDocsViewerApi(Consumer<ApiOptions> optionsConsumer) {
        return (ServletsViewerEndpointHandler) super.setupGroupDocsViewerApi(optionsConsumer);
    }

    @Override
    public ServletsViewerEndpointHandler setupGroupDocsViewerApi(Consumer<ApiOptions> optionsConsumer, ViewerFactory viewerFactory, ViewerControllerFactory viewerControllerFactory) {
        return (ServletsViewerEndpointHandler) super.setupGroupDocsViewerApi(optionsConsumer, viewerFactory, viewerControllerFactory);
    }

    @Override
    public ServletsViewerEndpointHandler setupLocalStorage(Path storagePath) {
        return (ServletsViewerEndpointHandler) super.setupLocalStorage(storagePath);
    }

    @Override
    public ServletsViewerEndpointHandler setupAwsS3Storage(Consumer<AwsS3Options> storageConfigConsumer) {
        return (ServletsViewerEndpointHandler) super.setupAwsS3Storage(storageConfigConsumer);
    }

    @Override
    public ServletsViewerEndpointHandler setupAzureBlobStorage(Consumer<AzureBlobOptions> storageConfigConsumer) {
        return (ServletsViewerEndpointHandler) super.setupAzureBlobStorage(storageConfigConsumer);
    }

    @Override
    public ServletsViewerEndpointHandler setupLocalCache(Consumer<LocalCacheConfig> cacheConfigConsumer) {
        return (ServletsViewerEndpointHandler) super.setupLocalCache(cacheConfigConsumer);
    }

    @Override
    public ServletsViewerEndpointHandler setupInMemoryCache(Consumer<InMemoryCacheConfig> cacheConfigConsumer) {
        return (ServletsViewerEndpointHandler) super.setupInMemoryCache(cacheConfigConsumer);
    }

    @Override
    public ServletsViewerEndpointHandler setupAwsS3Cache(Consumer<AwsS3Options> cacheConfigConsumer) {
        return (ServletsViewerEndpointHandler) super.setupAwsS3Cache(cacheConfigConsumer);
    }

    @Override
    public ServletsViewerEndpointHandler setupAzureBlobCache(Consumer<AzureBlobOptions> cacheConfigConsumer) {
        return (ServletsViewerEndpointHandler) super.setupAzureBlobCache(cacheConfigConsumer);
    }

    /**
     * Handles a viewer request by determining the action to be performed based on the request URL and executing the corresponding handler.
     *
     * @param servletRequest  the http servlet request from Servlets API.
     * @param servletResponse the http servlet response from Servlets API.
     * @throws ViewerUiException if an error occurs while handling the viewer request.
     */
    public void handleViewerRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        final String requestUrl = servletRequest.getRequestURI();
        final String queryString = servletRequest.getQueryString();
        try (final InputStream requestStream = servletRequest.getInputStream();
             final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream()) {
            if (isUploadRequest(requestUrl)) {
                handleUploadRequest(servletRequest, servletResponse, arrayOutputStream);
            } else {
                final int resultCode = super.handleViewerRequest(requestUrl, queryString, requestStream, servletResponse::addHeader, arrayOutputStream);
                servletResponse.setStatus(resultCode); // Must be set before writing to output stream
            }
            try (final OutputStream outputStream = servletResponse.getOutputStream()) {
                outputStream.write(arrayOutputStream.toByteArray());
            }
        } catch (Exception e) {
            LOGGER.error("Exception throws while handling viewer request: requestUrl={}, queryString={}", requestUrl, queryString, e);
            throw new ViewerUiException("Exception was thrown while handling request", e);
        }
    }


    private void handleUploadRequest(HttpServletRequest request, HttpServletResponse response,
                                     ByteArrayOutputStream arrayOutputStream) throws IOException, ServletException {
        final String fileName;
        final InputStream fileStream;
        final Part urlPart = request.getPart("url");
        if (urlPart == null) {
            final Part filePart = request.getPart("file");
            fileName = filePart.getSubmittedFileName();
            fileStream = filePart.getInputStream();
        } else {
            try (InputStream inputStream = urlPart.getInputStream()) {

                final String fileUrl = new String(StreamExtensions.toByteArray(inputStream), StandardCharsets.UTF_8);
                final URL url = new URL(fileUrl);
                final String urlFile = url.getFile();
                final int lastIndexOf = urlFile.lastIndexOf('/');
                final String fileNameWithParams = urlFile.substring(lastIndexOf == -1 ? 0 : lastIndexOf + 1);
                if (fileNameWithParams.contains("?")) {
                    fileName = fileNameWithParams.substring(0, fileNameWithParams.indexOf("?"));
                } else {
                    fileName = fileNameWithParams;
                }
                fileStream = url.openStream();
            }
        }
        try {
            final Part rewritePart = request.getPart("rewrite");
            boolean isRewrite;
            try (final InputStream inputStream = rewritePart.getInputStream()) {
                isRewrite = Boolean.toString(true).equals(new String(StreamExtensions.toByteArray(inputStream), StandardCharsets.UTF_8));
            }
            final int resultCode = handleViewerUploadRequest(fileStream, fileName, isRewrite, response::setHeader, arrayOutputStream);
            response.setStatus(resultCode);
        } finally {
            fileStream.close();
        }
    }
}
