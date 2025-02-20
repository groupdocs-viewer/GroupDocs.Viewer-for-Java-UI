package com.groupdocs.viewerui.ui.api.viewer;

import com.groupdocs.viewer.Viewer;
import com.groupdocs.viewer.interfaces.PageStreamFactory;
import com.groupdocs.viewer.interfaces.ResourceStreamFactory;
import com.groupdocs.viewer.options.HtmlViewOptions;
import com.groupdocs.viewer.options.JpgViewOptions;
import com.groupdocs.viewer.options.ViewInfoOptions;
import com.groupdocs.viewer.results.Resource;
import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.api.ApiNames;
import com.groupdocs.viewerui.ui.api.FileTypeResolver;
import com.groupdocs.viewerui.ui.api.configuration.ThumbSettings;
import com.groupdocs.viewerui.ui.api.internalcaching.InternalCache;
import com.groupdocs.viewerui.ui.api.licensing.ViewerLicenser;
import com.groupdocs.viewerui.ui.configuration.ApiOptions;
import com.groupdocs.viewerui.ui.configuration.ViewerConfig;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.PageFormatter;
import com.groupdocs.viewerui.ui.core.entities.*;
import com.groupdocs.viewerui.ui.core.extensions.CopyExtensions;
import com.groupdocs.viewerui.ui.core.extensions.UrlExtensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HtmlWithExternalResourcesViewer extends BaseViewer {
    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlWithExternalResourcesViewer.class);

    private final ViewerConfig _viewerConfig;

    private final ApiOptions _apiOptions;

    public HtmlWithExternalResourcesViewer(ViewerConfig viewerConfig, ApiOptions apiOptions, ViewerLicenser licenser, InternalCache internalCache, FileStorageProvider fileStorageProvider, FileTypeResolver fileTypeResolver, PageFormatter pageFormatter) {
        super(viewerConfig, licenser, internalCache, fileStorageProvider, fileTypeResolver, pageFormatter);
        _viewerConfig = viewerConfig;
        _apiOptions = apiOptions;
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
    protected Page renderPage(Viewer viewer, String filePath, int pageNumber) {
        String basePath = _apiOptions.getApiEndpoint();
        String actionName = ApiNames.API_METHOD_GET_RESOURCE;
        try {
            MemoryPageStreamFactory streamFactory = new MemoryPageStreamFactory(basePath, actionName, filePath);
            HtmlViewOptions viewOptions = HtmlViewOptions.forExternalResources(streamFactory, streamFactory);
            CopyExtensions.copyViewOptions(_viewerConfig.getHtmlViewOptions(), viewOptions);
            viewer.view(viewOptions, pageNumber);

            PageContents pageContents = streamFactory.getPageContents();
            Page page = createPage(pageNumber, pageContents.getPageData());
            for (Map.Entry<String, byte[]> resource : pageContents.getResources().entrySet()) {
                PageResource pageResource = new PageResource(resource.getKey(), resource.getValue());
                page.addResource(pageResource);
            }

            return page;
        } catch (Exception e) {
            LOGGER.error("Exception throws while rendering html page with external resources: filePath={}, pageNumber={}", filePath, pageNumber, e);
            throw new ViewerUiException(e);
        }
    }

    @Override
    protected Thumb renderThumb(Viewer viewer, String filePath, int pageNumber) {

        try (ByteArrayOutputStream thumbStream = new ByteArrayOutputStream()) {
            JpgViewOptions thumbViewOptions = createThumbViewOptions(thumbStream);
            viewer.view(thumbViewOptions, pageNumber);

            byte[] thumbBytes = thumbStream.toByteArray();
        if (thumbBytes.length == 0) {
            LOGGER.warn("Thumb for page {} of '{}' document has no data.", pageNumber, filePath);
        }

            Thumb thumb = createThumb(pageNumber, thumbBytes);
            return thumb;
        } catch (Exception e) {
            LOGGER.error("Exception throws while rendering thumb for html with external resources: filePath={}, pageNumber={}", filePath, pageNumber, e);
            throw new ViewerUiException(e);
        }
    }

    @Override
    protected ViewInfoOptions createViewInfoOptions() {
        return ViewInfoOptions.fromHtmlViewOptions(_viewerConfig.getHtmlViewOptions());
    }

    @Override
    public byte[] getPageResource(FileCredentials fileCredentials, int pageNumber, String resourceName) {
        Page page = getPage(fileCredentials, pageNumber);
        PageResource resource = page.getResource(resourceName);

        return resource.getData();
    }

    private JpgViewOptions createThumbViewOptions(OutputStream pageStream) {
        JpgViewOptions viewOptions = new JpgViewOptions(i -> pageStream,
                (i, closeable) -> { /*NOTE: Do nothing here*/ });

        CopyExtensions.copyBaseViewOptions(_viewerConfig.getHtmlViewOptions(), viewOptions);
        viewOptions.setExtractText(false);
        viewOptions.setQuality((byte) ThumbSettings.THUMB_QUALITY);
        viewOptions.setMaxWidth(ThumbSettings.MAX_THUMB_WIDTH);
        viewOptions.setMaxHeight(ThumbSettings.MAX_THUMB_HEIGHT);

        return viewOptions;
    }

    private class MemoryPageStreamFactory implements PageStreamFactory, ResourceStreamFactory {

        private final String _basePath;

        private final String _actionName;

        private final String _filePath;

        private final PageContents _pageContents;

        public MemoryPageStreamFactory(String basePath, String actionName, String filePath) {
            _basePath = basePath;
            _actionName = actionName;
            _filePath = UrlExtensions.encode(filePath);
            _pageContents = new PageContents();
        }

        public PageContents getPageContents() {
            return _pageContents;
        }

        public OutputStream createPageStream(int pageNumber) {
            return _pageContents.getPageStream();
        }

        public void closePageStream(int pageNumber, OutputStream pageStream) {
            _pageContents.flushPageStream(pageStream);
            try {
                pageStream.close();
            } catch (IOException e) {
                LOGGER.error("Exception throws while closing page stream: pageNumber={}", pageNumber, e);
            }
        }

        public OutputStream createResourceStream(int pageNumber, Resource resource) {
            return _pageContents.getResourceStream(resource.getFileName());
        }

        public String createResourceUrl(int pageNumber, Resource resource) {
            return _basePath + "/" + _actionName + "?guid=" + _filePath + "&pageNumber=" + pageNumber + "&resourceName=" + resource.getFileName();
        }

        public void closeResourceStream(int pageNumber, Resource resource, OutputStream resourceStream) {
            final String fileName = resource.getFileName();
            _pageContents.flushResourceStream(fileName, resourceStream);
            try {
                resourceStream.close();
            } catch (IOException e) {
                LOGGER.error("Exception throws while closing resource stream: fileName={}", fileName, e);
            }
        }
    }

    private class PageContents {

        public Map<String, byte[]> _resources = new HashMap<>();

        private byte[] _pageData;

        public Map<String, byte[]> getResources() {
            return _resources;
        }

        public void setResources(Map<String, byte[]> _resources) {
            this._resources = _resources;
        }

        public byte[] getPageData() {
            return _pageData;
        }

        public OutputStream getPageStream() {
            _pageData = new byte[0];
            return new ByteArrayOutputStream();
        }

        public void setPageStream(byte[] _pageStream) {
            this._pageData = _pageStream;
        }

        public OutputStream getResourceStream(String fileName) {
            _resources.put(fileName, new byte[0]);
            return new ByteArrayOutputStream();
        }

        public void flushResourceStream(String fileName, OutputStream resourceStream) {
            if (resourceStream instanceof ByteArrayOutputStream) {
                _resources.put(fileName, ((ByteArrayOutputStream) resourceStream).toByteArray());
            } else {
                LOGGER.error("Unexpected type of resource stream: fileName={}", fileName);
                throw new ViewerUiException("Unexpected type of resource stream!");
            }
        }

        public void flushPageStream(OutputStream pageStream) {
            if (pageStream instanceof ByteArrayOutputStream) {
                _pageData = ((ByteArrayOutputStream) pageStream).toByteArray();
            } else {
                LOGGER.error("Unexpected type of page stream: pageStream={}", pageStream);
                throw new ViewerUiException("Unexpected type of page stream!");
            }
        }
    }
}
