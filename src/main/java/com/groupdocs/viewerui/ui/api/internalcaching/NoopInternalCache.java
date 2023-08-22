package com.groupdocs.viewerui.ui.api.internalcaching;

import com.groupdocs.viewer.Viewer;
import com.groupdocs.viewerui.ui.core.entities.FileCredentials;

public class NoopInternalCache implements IInternalCache {

	@Override
	public Viewer get(FileCredentials fileCredentials) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void set(FileCredentials fileCredentials, Viewer entry) {
		throw new RuntimeException("Not implemented");
	}

}
