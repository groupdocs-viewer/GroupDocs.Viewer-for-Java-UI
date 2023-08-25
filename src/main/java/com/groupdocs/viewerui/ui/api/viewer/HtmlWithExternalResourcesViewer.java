package com.groupdocs.viewerui.ui.api.viewer;

import com.groupdocs.viewer.Viewer;
import com.groupdocs.viewer.interfaces.PageStreamFactory;
import com.groupdocs.viewer.interfaces.ResourceStreamFactory;
import com.groupdocs.viewer.options.HtmlViewOptions;
import com.groupdocs.viewer.options.ViewInfoOptions;
import com.groupdocs.viewer.results.Resource;
import com.groupdocs.viewerui.ui.api.Constants;
import com.groupdocs.viewerui.ui.api.FileTypeResolver;
import com.groupdocs.viewerui.ui.api.internalcaching.InternalCache;
import com.groupdocs.viewerui.ui.api.licensing.ViewerLicenser;
import com.groupdocs.viewerui.ui.configuration.ApiOptions;
import com.groupdocs.viewerui.ui.configuration.ViewerConfig;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.PageFormatter;
import com.groupdocs.viewerui.ui.core.entities.FileCredentials;
import com.groupdocs.viewerui.ui.core.entities.HtmlPage;
import com.groupdocs.viewerui.ui.core.entities.Page;
import com.groupdocs.viewerui.ui.core.entities.PageResource;
import com.groupdocs.viewerui.ui.core.extensions.CopyExtensions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HtmlWithExternalResourcesViewer extends BaseViewer {

    private final ViewerConfig _viewerConfig;

    private final ApiOptions _apiOptions;

    public HtmlWithExternalResourcesViewer(ViewerConfig viewerConfig, ApiOptions apiOptions, ViewerLicenser licenser, InternalCache internalCache, FileStorageProvider fileStorageProvider, FileTypeResolver fileTypeResolver, PageFormatter pageFormatter) {
        super(viewerConfig, licenser, internalCache, fileStorageProvider, fileTypeResolver, pageFormatter);
        _viewerConfig = viewerConfig;
        _apiOptions = apiOptions;
    }

    @Override
    public String getPageExtension() {
        return HtmlPage.EXTENSION;
    }

    @Override
    public Page createPage(int pageNumber, byte[] data) {
        return new HtmlPage(pageNumber, data);
    }

    @Override
    protected Page renderPage(Viewer viewer, String filePath, int pageNumber) {
        String basePath = _apiOptions.getApiEndpoint();
        String actionName = Constants.LOAD_DOCUMENT_PAGE_RESOURCE_ACTION_NAME;
        try {
            MemoryPageStreamFactory streamFactory = new MemoryPageStreamFactory(basePath, actionName, filePath);
            HtmlViewOptions viewOptions = HtmlViewOptions.forExternalResources(streamFactory, streamFactory);
            CopyExtensions.copyHtmlViewOptions(_viewerConfig.getHtmlViewOptions(), viewOptions);
            viewer.view(viewOptions, pageNumber);

            PageContents pageContents = streamFactory.getPageContents();
            Page page = createPage(pageNumber, pageContents.getPageData());
            for (Map.Entry<String, byte[]> resource : pageContents.getResources().entrySet()) {
                PageResource pageResource = new PageResource(resource.getKey(), resource.getValue());
                page.addResource(pageResource);
            }

            return page;
        } catch (Exception e) {
            e.printStackTrace(); // TODO: Add logging
            throw new RuntimeException(e);
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

    private class MemoryPageStreamFactory implements PageStreamFactory, ResourceStreamFactory {

        private final String _basePath;

        private final String _actionName;

        private final String _filePath;

        private final PageContents _pageContents;

        public MemoryPageStreamFactory(String basePath, String actionName, String filePath) {
            _basePath = basePath;
            _actionName = actionName;
            _filePath = URLEncoder.encode(filePath, StandardCharsets.UTF_8);
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
                e.printStackTrace(); // TODO: Add logging
                throw new RuntimeException(e);
            }
        }

        public OutputStream createResourceStream(int pageNumber, Resource resource) {
            return _pageContents.getResourceStream(resource.getFileName());
        }

        public String createResourceUrl(int pageNumber, Resource resource) {
            return _basePath + "/" + _actionName + "?guid=" + _filePath + "&pageNumber=" + pageNumber + "&resourceName=" + resource.getFileName();
        }

        public void closeResourceStream(int pageNumber, Resource resource, OutputStream resourceStream) {
            _pageContents.flushResourceStream(resource.getFileName(), resourceStream);
            try {
                resourceStream.close();
            } catch (IOException e) {
                e.printStackTrace(); // TODO: Add logging
                throw new RuntimeException(e);
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
                // TODO: log warning
            }
        }

        public void flushPageStream(OutputStream pageStream) {
            if (pageStream instanceof ByteArrayOutputStream) {
                _pageData = ((ByteArrayOutputStream) pageStream).toByteArray();
            } else {
                // TODO: log warning
            }
        }
    }
}
