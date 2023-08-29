package com.groupdocs.viewerui.ui.api.factory;

import com.groupdocs.viewerui.ui.api.FileTypeResolver;
import com.groupdocs.viewerui.ui.api.FileTypeResolverFactory;
import com.groupdocs.viewerui.ui.api.cache.FileCache;
import com.groupdocs.viewerui.ui.api.cache.FileCacheFactory;
import com.groupdocs.viewerui.ui.api.internalcaching.InternalCache;
import com.groupdocs.viewerui.ui.api.internalcaching.InternalCacheFactory;
import com.groupdocs.viewerui.ui.api.licensing.ViewerLicenser;
import com.groupdocs.viewerui.ui.api.licensing.ViewerLicenserFactory;
import com.groupdocs.viewerui.ui.api.viewer.HtmlWithEmbeddedResourcesViewer;
import com.groupdocs.viewerui.ui.api.viewer.HtmlWithExternalResourcesViewer;
import com.groupdocs.viewerui.ui.api.viewer.JpgViewer;
import com.groupdocs.viewerui.ui.api.viewer.PngViewer;
import com.groupdocs.viewerui.ui.configuration.ApiOptions;
import com.groupdocs.viewerui.ui.configuration.InternalCacheOptions;
import com.groupdocs.viewerui.ui.configuration.ViewerConfig;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.IViewer;
import com.groupdocs.viewerui.ui.core.PageFormatter;
import com.groupdocs.viewerui.ui.core.cache.internal.MemoryCache;
import com.groupdocs.viewerui.ui.core.cache.internal.MemoryCacheFactory;
import com.groupdocs.viewerui.ui.core.caching.CachingViewer;
import com.groupdocs.viewerui.ui.core.pageformatting.PageFormatterFactory;

public class DefaultViewerFactory implements ViewerFactory {

    @Override
    public IViewer createViewer(ViewerConfig viewerConfig, ApiOptions apiOptions, FileStorageProvider fileStorageProvider) {
        IViewer viewer = null;

        final MemoryCache memoryCache = MemoryCacheFactory.getInstance();
        final InternalCache internalCache = InternalCacheFactory.getInstance(
                memoryCache, InternalCacheOptions.CACHE_FOR_FIVE_MINUTES);
        final ViewerLicenser viewerLicenser = ViewerLicenserFactory.getInstance(viewerConfig);
        final FileTypeResolver fileTypeResolver = FileTypeResolverFactory.getInstance();
        final PageFormatter pageFormatter = PageFormatterFactory.getInstance();

        switch (viewerConfig.getViewerType()) {
            case HTML_WITH_EXTERNAL_RESOURCES:
                viewer = new HtmlWithExternalResourcesViewer(viewerConfig, apiOptions, viewerLicenser, internalCache, fileStorageProvider, fileTypeResolver, pageFormatter);
                break;
            case PNG:
                viewer = new PngViewer(viewerConfig, viewerLicenser, internalCache, fileStorageProvider, fileTypeResolver, pageFormatter);
                break;
            case JPG:
                viewer = new JpgViewer(viewerConfig, viewerLicenser, internalCache, fileStorageProvider, fileTypeResolver, pageFormatter);
                break;
            default:
                viewer = new HtmlWithEmbeddedResourcesViewer(viewerConfig, viewerLicenser, internalCache, fileStorageProvider, fileTypeResolver, pageFormatter);
                break;
        }

        final FileCache fileCache = FileCacheFactory.newInstance();
        return new CachingViewer(viewer, fileCache);
    }

}
