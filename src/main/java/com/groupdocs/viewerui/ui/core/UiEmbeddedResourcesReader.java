package com.groupdocs.viewerui.ui.core;

import com.groupdocs.viewerui.Keys;
import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.core.extensions.StreamExtensions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UiEmbeddedResourcesReader implements IUiResourcesReader {

    private static final String BASIC_RESOURCE_PATH = "com/groupdocs/viewerui";
    private List<UiResource> _cachedUiResources = null;
    private IContentTypeDetector _contentTypeDetector;

    @Override
    public UiResource getUiResource(String resourceName) throws IOException {
        if (_cachedUiResources == null) {
            _cachedUiResources = new ArrayList<>();
            final UiResource uiResource = loadAndAddUiResource(getClass().getClassLoader(), resourceName, getContentTypeDetector());
            _cachedUiResources.add(uiResource);
            return uiResource;
        } else {
            final Optional<UiResource> firstResource = _cachedUiResources.stream().filter(uiResource -> resourceName.equals(uiResource.getFileName())).findFirst();
            if (firstResource.isPresent()) {
                return firstResource.get();
            } else {
                final UiResource uiResource = loadAndAddUiResource(getClass().getClassLoader(), resourceName, getContentTypeDetector());
                _cachedUiResources.add(uiResource);
                return uiResource;
            }
        }
    }

    private static UiResource loadAndAddUiResource(ClassLoader classLoader, String resourceName, IContentTypeDetector contentTypeDetector) throws IOException {
        try (final InputStream resourceAsStream = classLoader
                .getResourceAsStream(BASIC_RESOURCE_PATH + "/" + resourceName)) {
            if (resourceAsStream == null) {
                throw new ViewerUiException(
                        "Resource with name '" + Keys.GROUPDOCSVIEWERUI_MAIN_UI_RESOURCE + "' was not found");
            }
            final String contentType = contentTypeDetector.detect(resourceName);
            final byte[] resourceContent = StreamExtensions.toByteArray(resourceAsStream);
            final UiResource uiResource = UiResource.create(resourceName, resourceContent, contentType);
            return uiResource;
        }
    }

    public IContentTypeDetector getContentTypeDetector() {
        if (_contentTypeDetector == null) {
            _contentTypeDetector = new DefaultContentTypeDetector();
        }
        return _contentTypeDetector;
    }

    public void setContentTypeDetector(IContentTypeDetector contentTypeDetector) {
        this._contentTypeDetector = contentTypeDetector;
    }
}
