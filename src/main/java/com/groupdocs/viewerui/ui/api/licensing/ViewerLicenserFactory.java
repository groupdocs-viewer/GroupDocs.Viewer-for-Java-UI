package com.groupdocs.viewerui.ui.api.licensing;

import com.groupdocs.viewerui.ui.configuration.ViewerConfig;

public class ViewerLicenserFactory {
    private static ViewerLicenser _viewerLicenser;

    private ViewerLicenserFactory() {
    }

    public static synchronized ViewerLicenser getInstance(ViewerConfig viewerConfig) {
        if (_viewerLicenser == null) {
            _viewerLicenser = new DefaultViewerLicenser(viewerConfig);
        }
        return _viewerLicenser;
    }

    public static void setInstance(ViewerLicenser viewerLicenser) {
        ViewerLicenserFactory._viewerLicenser = viewerLicenser;
    }
}
