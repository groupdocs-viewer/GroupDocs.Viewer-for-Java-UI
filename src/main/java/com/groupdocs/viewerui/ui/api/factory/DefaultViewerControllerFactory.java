package com.groupdocs.viewerui.ui.api.factory;

import com.groupdocs.viewerui.ui.api.FilePathFileNameResolver;
import com.groupdocs.viewerui.ui.api.SearchTermResolver;
import com.groupdocs.viewerui.ui.api.UiConfigProvider;
import com.groupdocs.viewerui.ui.api.controller.ViewerController;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.IViewer;
import com.groupdocs.viewerui.ui.core.configuration.Config;

public class DefaultViewerControllerFactory implements ViewerControllerFactory {

	@Override
	public ViewerController createViewerController(Config config, IViewer viewer, FileStorageProvider fileStorageProvider) {

		return new ViewerController(fileStorageProvider, new FilePathFileNameResolver(), new SearchTermResolver(),
				new UiConfigProvider(), viewer, config);
	}

}
