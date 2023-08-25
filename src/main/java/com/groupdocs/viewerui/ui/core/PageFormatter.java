package com.groupdocs.viewerui.ui.core;

import com.groupdocs.viewerui.ui.core.entities.FileCredentials;
import com.groupdocs.viewerui.ui.core.entities.Page;

public interface PageFormatter {

	Page format(FileCredentials fileCredentials, Page page);

}
