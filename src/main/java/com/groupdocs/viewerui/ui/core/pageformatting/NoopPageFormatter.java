package com.groupdocs.viewerui.ui.core.pageformatting;

import com.groupdocs.viewerui.ui.core.IPageFormatter;
import com.groupdocs.viewerui.ui.core.entities.FileCredentials;
import com.groupdocs.viewerui.ui.core.entities.Page;

public class NoopPageFormatter implements IPageFormatter {

	public Page format(FileCredentials fileCredentials, Page page) {
		return page;
	}

}
