package com.groupdocs.viewerui.ui.api.internalcaching;

import com.groupdocs.viewer.Viewer;
import com.groupdocs.viewerui.ui.core.entities.FileCredentials;

public interface InternalCache {

	Viewer get(FileCredentials fileCredentials);

	void set(FileCredentials fileCredentials, Viewer entry);

}
