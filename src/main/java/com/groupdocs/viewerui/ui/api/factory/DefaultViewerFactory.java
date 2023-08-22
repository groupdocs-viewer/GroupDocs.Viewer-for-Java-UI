package com.groupdocs.viewerui.ui.api.factory;

import com.groupdocs.viewerui.ui.api.FileExtensionFileTypeResolver;
import com.groupdocs.viewerui.ui.api.cache.InMemoryFileCache;
import com.groupdocs.viewerui.ui.api.cache.config.CacheConfig;
import com.groupdocs.viewerui.ui.api.internalcaching.InMemoryInternalCache;
import com.groupdocs.viewerui.ui.api.licensing.ViewerLicenser;
import com.groupdocs.viewerui.ui.api.viewer.HtmlWithEmbeddedResourcesViewer;
import com.groupdocs.viewerui.ui.api.viewer.HtmlWithExternalResourcesViewer;
import com.groupdocs.viewerui.ui.api.viewer.JpgViewer;
import com.groupdocs.viewerui.ui.api.viewer.PngViewer;
import com.groupdocs.viewerui.ui.configuration.ApiOptions;
import com.groupdocs.viewerui.ui.configuration.InternalCacheOptions;
import com.groupdocs.viewerui.ui.configuration.ViewerConfig;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.IViewer;
import com.groupdocs.viewerui.ui.core.caching.CachingViewer;
import com.groupdocs.viewerui.ui.core.pageformatting.NoopPageFormatter;

public class DefaultViewerFactory implements ViewerFactory {

    @Override
    public IViewer createViewer(ViewerConfig viewerConfig, ApiOptions apiOptions, FileStorageProvider fileStorageProvider) {
        IViewer viewer = null;
        switch (viewerConfig.getViewerType()) {
            case HTML_WITH_EXTERNAL_RESOURCES:
                viewer = new HtmlWithExternalResourcesViewer(viewerConfig, apiOptions, new ViewerLicenser(viewerConfig),
                        new InMemoryInternalCache(InternalCacheOptions.CACHE_FOR_FIVE_MINUTES), fileStorageProvider,
                        new FileExtensionFileTypeResolver(), new NoopPageFormatter());
                break;
            case PNG:
                viewer = new PngViewer(viewerConfig, new ViewerLicenser(viewerConfig),
                        new InMemoryInternalCache(InternalCacheOptions.CACHE_FOR_FIVE_MINUTES), fileStorageProvider,
                        new FileExtensionFileTypeResolver(), new NoopPageFormatter());
                break;
            case JPG:
                viewer = new JpgViewer(viewerConfig, new ViewerLicenser(viewerConfig),
                        new InMemoryInternalCache(InternalCacheOptions.CACHE_FOR_FIVE_MINUTES), fileStorageProvider,
                        new FileExtensionFileTypeResolver(), new NoopPageFormatter());
                break;
            default:
                viewer = new HtmlWithEmbeddedResourcesViewer(viewerConfig, new ViewerLicenser(viewerConfig),
                        new InMemoryInternalCache(InternalCacheOptions.CACHE_FOR_FIVE_MINUTES), fileStorageProvider,
                        new FileExtensionFileTypeResolver(), new NoopPageFormatter());
                break;
        }

        return new CachingViewer(
                viewer,
                new InMemoryFileCache(new CacheConfig())
        );
    }

}
