package com.groupdocs.viewerui.ui.api.factory;

import com.groupdocs.viewerui.ui.api.controller.ViewerController;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.IViewer;
import com.groupdocs.viewerui.ui.core.configuration.Config;

public interface ViewerControllerFactory {

	ViewerController createViewerController(Config config, IViewer viewer, FileStorageProvider fileStorageProvider);

}
