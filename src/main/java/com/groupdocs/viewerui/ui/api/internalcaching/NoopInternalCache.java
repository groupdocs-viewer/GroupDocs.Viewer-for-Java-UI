package com.groupdocs.viewerui.ui.api.internalcaching;

import com.groupdocs.viewer.Viewer;
import com.groupdocs.viewerui.ui.core.entities.FileCredentials;

public class NoopInternalCache implements InternalCache {

	@Override
	public Viewer get(FileCredentials fileCredentials) {
		return null;
	}

	@Override
	public void set(FileCredentials fileCredentials, Viewer entry) {
	}
}
