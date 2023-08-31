package com.groupdocs.viewerui.handler;

import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.api.factory.ViewerControllerFactory;
import com.groupdocs.viewerui.ui.api.factory.ViewerFactory;
import com.groupdocs.viewerui.ui.configuration.ApiOptions;
import com.groupdocs.viewerui.ui.configuration.UiOptions;
import com.groupdocs.viewerui.ui.configuration.ViewerConfig;
import com.groupdocs.viewerui.ui.core.cache.local.config.LocalCacheConfig;
import com.groupdocs.viewerui.ui.core.cache.memory.config.InMemoryCacheConfig;
import com.groupdocs.viewerui.ui.core.configuration.Config;
import com.groupdocs.viewerui.ui.core.extensions.StreamExtensions;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Endpoint handler for web applications which use Jakarta API
 */
public class JakartaViewerEndpointHandler extends CommonViewerEndpointHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(JakartaViewerEndpointHandler.class);

    protected JakartaViewerEndpointHandler() {
    }

    /**
     * Sets up the GroupDocs.Viewer for Java UI in common.
     *
     * @param configConsumer a consumer that accepts a ViewerConfig and a Config object to apply the configuration settings.
     * @return a reference to `this` object.
     */
    public static JakartaViewerEndpointHandler setupGroupDocsViewer(BiConsumer<ViewerConfig, Config> configConsumer) {
        return CommonViewerEndpointHandler.setupGroupDocsViewer(new JakartaViewerEndpointHandler(), configConsumer);
    }

    @Override
    public JakartaViewerEndpointHandler setupGroupDocsViewerUI(Consumer<UiOptions> optionsConsumer) {
        return (JakartaViewerEndpointHandler) super.setupGroupDocsViewerUI(optionsConsumer);
    }

    @Override
    public JakartaViewerEndpointHandler setupGroupDocsViewerApi(Consumer<ApiOptions> optionsConsumer) {
        return (JakartaViewerEndpointHandler) super.setupGroupDocsViewerApi(optionsConsumer);
    }

    @Override
    public JakartaViewerEndpointHandler setupGroupDocsViewerApi(Consumer<ApiOptions> optionsConsumer, ViewerFactory viewerFactory, ViewerControllerFactory viewerControllerFactory) {
        return (JakartaViewerEndpointHandler) super.setupGroupDocsViewerApi(optionsConsumer, viewerFactory, viewerControllerFactory);
    }

    @Override
    public JakartaViewerEndpointHandler setupLocalStorage(Path storagePath) {
        return (JakartaViewerEndpointHandler) super.setupLocalStorage(storagePath);
    }

    @Override
    public JakartaViewerEndpointHandler setupLocalCache(Consumer<LocalCacheConfig> cacheConfigConsumer) {
        return (JakartaViewerEndpointHandler) super.setupLocalCache(cacheConfigConsumer);
    }

    @Override
    public JakartaViewerEndpointHandler setupInMemoryCache(Consumer<InMemoryCacheConfig> cacheConfigConsumer) {
        return (JakartaViewerEndpointHandler) super.setupInMemoryCache(cacheConfigConsumer);
    }

    /**
     * Handles a viewer request by determining the action to be performed based on the request URL and executing the corresponding handler.
     *
     * @param servletRequest  the http servlet request from Jakarta API.
     * @param servletResponse the http servlet response from Jakarta API.
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
                fileName = urlFile.substring(lastIndexOf == -1 ? 0 : lastIndexOf + 1);
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
