package com.groupdocs.viewerui.ui.api;

public class UiConfigProviderFactory {
    private static UiConfigProvider _uiConfigProvider;

    public static synchronized UiConfigProvider getInstance() {
        if (_uiConfigProvider == null) {
            _uiConfigProvider = new DefaultUiConfigProvider();
        }
        return _uiConfigProvider;
    }

    public static void setInstance(UiConfigProvider uiConfigProvider) {
        UiConfigProviderFactory._uiConfigProvider = uiConfigProvider;
    }
}
