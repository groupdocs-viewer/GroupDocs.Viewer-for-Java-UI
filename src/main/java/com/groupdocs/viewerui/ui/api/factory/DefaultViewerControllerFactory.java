package com.groupdocs.viewerui.ui.api.factory;

import com.groupdocs.viewerui.ui.api.FileNameResolverFactory;
import com.groupdocs.viewerui.ui.api.SearchTermResolverFactory;
import com.groupdocs.viewerui.ui.api.controller.ViewerController;
import com.groupdocs.viewerui.ui.api.utils.IApiUrlBuilder;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.IViewer;
import com.groupdocs.viewerui.ui.core.configuration.Config;

public class DefaultViewerControllerFactory implements ViewerControllerFactory {

    @Override
    public ViewerController createViewerController(Config config, IViewer viewer, FileStorageProvider fileStorageProvider, IApiUrlBuilder apiUrlBuilder) {

        return new ViewerController(fileStorageProvider,
                FileNameResolverFactory.getInstance(),
                SearchTermResolverFactory.getInstance(),
                viewer,
                config,
                apiUrlBuilder
        );
    }

}
