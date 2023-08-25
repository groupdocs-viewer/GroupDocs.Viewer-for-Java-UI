package com.groupdocs.viewerui.ui.core.pageformatting;

import com.groupdocs.viewerui.ui.core.PageFormatter;
import com.groupdocs.viewerui.ui.core.entities.FileCredentials;
import com.groupdocs.viewerui.ui.core.entities.Page;

public class NoopPageFormatter implements PageFormatter {

	public Page format(FileCredentials fileCredentials, Page page) {
		return page;
	}

}
