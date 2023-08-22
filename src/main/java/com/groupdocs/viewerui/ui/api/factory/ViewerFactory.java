package com.groupdocs.viewerui.ui.api.factory;

import com.groupdocs.viewerui.ui.configuration.ApiOptions;
import com.groupdocs.viewerui.ui.configuration.ViewerConfig;
import com.groupdocs.viewerui.ui.core.FileStorageProvider;
import com.groupdocs.viewerui.ui.core.IViewer;

public interface ViewerFactory {

	IViewer createViewer(ViewerConfig viewerConfig, ApiOptions apiOptions, FileStorageProvider fileStorageProvider);

}
