package com.groupdocs.viewerui.ui.core.configuration;

public class RenderingMode {
    public static final RenderingMode Html = new RenderingMode("html");
    public static final RenderingMode Image = new RenderingMode("image");
    private final String _value;

    public RenderingMode(String value) {
        this._value = value;
    }

    public String getValue() {
        return _value;
    }
}
