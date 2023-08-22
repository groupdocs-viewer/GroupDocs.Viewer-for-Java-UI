package com.groupdocs.viewerui.ui.core;

import com.groupdocs.viewerui.ui.configuration.ApiOptions;
import com.groupdocs.viewerui.ui.configuration.UiOptions;
import com.groupdocs.viewerui.ui.core.extensions.StringExtensions;

public class DefaultActionNameDetector implements IActionNameDetector {
    private UiOptions _uiOptions;
    private ApiOptions _apiOptions;

    public DefaultActionNameDetector(UiOptions uiOptions, ApiOptions apiOptions) {
        this._uiOptions = uiOptions;
        this._apiOptions = apiOptions;
    }

    @Override
    public ActionName detectActionName(String requestUrl) {
        final UiOptions uiOptions = getUiOptions();
        if (requestUrl.startsWith(StringExtensions.withTrailingSlash(uiOptions.getUiPath()))) {
            return ActionName.UI_RESOURCE;
        } else if (requestUrl.endsWith(ActionName.LOAD_CONFIG.getUrl())) {
            return ActionName.LOAD_CONFIG;
        } else if (isApiRequest(requestUrl)) {
            for (ActionName actionName : ActionName.values()) {
                if (!requestUrl.isEmpty() && requestUrl.endsWith(actionName.getUrl())) {
                    return actionName;
                }
            }
        }
        return null;
    }

    private boolean isApiRequest(String requestUrl) {
        final ApiOptions apiOptions = getApiOptions();
        final String apiEndpoint = apiOptions.getApiEndpoint();
        return requestUrl.startsWith(StringExtensions.withTrailingSlash(apiEndpoint));
    }

    public UiOptions getUiOptions() {
        return _uiOptions;
    }

    public void setUiOptions(UiOptions uiOptions) {
        this._uiOptions = uiOptions;
    }

    public ApiOptions getApiOptions() {
        return _apiOptions;
    }

    public void setApiOptions(ApiOptions apiOptions) {
        this._apiOptions = apiOptions;
    }
}
