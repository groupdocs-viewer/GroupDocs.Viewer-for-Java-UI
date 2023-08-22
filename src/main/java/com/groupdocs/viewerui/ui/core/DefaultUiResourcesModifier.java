package com.groupdocs.viewerui.ui.core;

import com.groupdocs.viewerui.Keys;
import com.groupdocs.viewerui.ui.configuration.ApiOptions;
import com.groupdocs.viewerui.ui.configuration.UiOptions;
import com.groupdocs.viewerui.ui.core.extensions.StringExtensions;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultUiResourcesModifier implements IUiResourcesModifier {

	private final UiOptions _uiOptions;

	private final ApiOptions _apiOptions;

	public DefaultUiResourcesModifier(UiOptions uiOptions, ApiOptions apiOptions) {
		this._uiOptions = uiOptions;
		this._apiOptions = apiOptions;
	}

	@Override
	public void modifyResource(UiResource resource, String pathBase) {
		if (Keys.GROUPDOCSVIEWERUI_MAIN_UI_RESOURCE.equals(resource.getFileName())) {
			modifyIndexPageHtml(resource, pathBase);
			injectCustomStylesheets(resource);
		}
	}

	private void modifyIndexPageHtml(UiResource indexResource, String pathBase) {

		final String uiPath = StringExtensions.withTrailingSlash(pathBase + _uiOptions.getUiPath());

		String modifiedContent = indexResource.getContent();

		modifiedContent = modifiedContent.replace(Keys.GROUPDOCSVIEWERUI_MAIN_UI_PATH, uiPath);

		final String apiPath = StringExtensions.trimTrailingSlash(pathBase + _apiOptions.getApiEndpoint());

		modifiedContent = modifiedContent.replace(Keys.GROUPDOCSVIEWERUI_MAIN_UI_API_TARGET, apiPath);

		final String uiConfigPath = pathBase + _uiOptions.getUiConfigEndpoint();

		modifiedContent = modifiedContent.replace(Keys.GROUPDOCSVIEWERUI_MAIN_UI_SETTINGS_PATH_TARGET, uiConfigPath);

		indexResource.setContent(modifiedContent);
	}

	private void injectCustomStylesheets(UiResource resource) {

		final String resourceContent = resource.getContent();
		if (_uiOptions.getCustomStylesheets().isEmpty()) {
			resource.setContent(resourceContent.replace(Keys.GROUPDOCSVIEWERUI_STYLESHEETS_TARGET, ""));
		}

		final Stream<UiStylesheet> styleSheets = _uiOptions.getCustomStylesheets()
			.stream()
			.map(stylesheet -> UiStylesheet.create(_uiOptions, stylesheet));

		Stream<String> htmlStyles = styleSheets.map(uiStylesheet -> {
			String linkHref = StringExtensions.asRelativeResource(uiStylesheet.getResourceRelativePath());
			return "<link rel='stylesheet' href='" + linkHref + "'/>";
		});

		resource.setContent(resourceContent.replace(Keys.GROUPDOCSVIEWERUI_STYLESHEETS_TARGET,
				htmlStyles.collect(Collectors.joining("\n"))));
	}

}
