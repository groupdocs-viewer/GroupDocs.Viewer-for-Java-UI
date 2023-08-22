package com.groupdocs.viewerui.ui.core;

import java.io.IOException;

public interface IUiResourcesReader {

	UiResource getUiResource(String resourceName) throws IOException;

}
