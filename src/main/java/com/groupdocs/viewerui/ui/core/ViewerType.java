package com.groupdocs.viewerui.ui.core;

import com.groupdocs.viewerui.ui.core.configuration.RenderingMode;

public enum ViewerType {

    HTML_WITH_EMBEDDED_RESOURCES, HTML_WITH_EXTERNAL_RESOURCES, PNG, JPG,
    ;

    public RenderingMode toRenderingMode() {
        if (this == HTML_WITH_EXTERNAL_RESOURCES || this == HTML_WITH_EMBEDDED_RESOURCES) {
            return RenderingMode.Html;
        }

        return RenderingMode.Image;
    }
}
