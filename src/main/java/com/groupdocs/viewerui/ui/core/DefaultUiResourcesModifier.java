package com.groupdocs.viewerui.ui.core;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupdocs.viewerui.Keys;
import com.groupdocs.viewerui.ui.configuration.ApiOptions;
import com.groupdocs.viewerui.ui.configuration.UiOptions;
import com.groupdocs.viewerui.ui.core.configuration.Config;
import com.groupdocs.viewerui.ui.core.extensions.StringExtensions;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultUiResourcesModifier implements IUiResourcesModifier {

	private final UiOptions _uiOptions;

	private final ApiOptions _apiOptions;

	public DefaultUiResourcesModifier(UiOptions uiOptions, ApiOptions apiOptions) {
		this._uiOptions = uiOptions;
		this._apiOptions = apiOptions;
	}

	private static String serializeWindowConfig(String apiEndpoint, Config config) {
		WindowConfig windowConfig = new WindowConfig(
				apiEndpoint,
				config.getRenderingMode().getValue(),
				config.isStaticContentMode(),
				config.getInitialFile(),
				config.getPreloadPages(),
				config.isEnableHeader(),
				config.isEnableToolbar(),
				config.isEnablePageSelector(),
				config.isEnableDownloadPdf(),
				config.isEnableFileUpload(),
				config.isEnableFileBrowser(),
				config.isEnableContextMenu(),
				config.isEnableZoom(),
				config.isEnableSearch(),
				config.isEnableFileName(),
				config.isEnableThumbnails(),
				config.isEnablePrint(),
				config.isEnablePresentation(),
				config.isEnableHyperlinks(),
				config.isEnableLanguageSelector(),
				config.getDefaultLanguage().getValue(),
				Arrays.stream(config.getSupportedLanguages()).map(languageCode -> languageCode.getValue()).collect(Collectors.toList()));

		final ObjectMapper objectMapper = new ObjectMapper();

		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		String json = null;
		try {
			json = objectMapper.writeValueAsString(windowConfig);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return json;
	}

	@Override
	public void modifyResource(UiResource resource, Config config, String pathBase) {
		if (Keys.GROUPDOCSVIEWERUI_MAIN_UI_RESOURCE.equals(resource.getFileName())) {
			modifyIndexPageHtml(resource, config, pathBase);
			injectCustomStylesheets(resource);
		}
	}

	private void modifyIndexPageHtml(UiResource indexResource, Config config, String pathBase) {

		final String uiPath = StringExtensions.withTrailingSlash(pathBase + _uiOptions.getUiPath());

		String modifiedContent = indexResource.getContentAsString();

		modifiedContent = modifiedContent.replace(Keys.GROUPDOCSVIEWERUI_MAIN_UI_PATH, uiPath);
		modifiedContent = modifiedContent.replace(Keys.GROUPDOCSVIEWERUI_MAIN_UI_TITLE, _uiOptions.getUiTitle());

		final String apiEndpoint = _apiOptions.getApiEndpoint();
		final String uiConfig = serializeWindowConfig(apiEndpoint, config);

		modifiedContent = modifiedContent.replace(Keys.GROUPDOCSVIEWERUI_MAIN_UI_CONFIG, uiConfig);

		indexResource.setContent(modifiedContent.getBytes(StandardCharsets.UTF_8));
	}

	private void injectCustomStylesheets(UiResource resource) {

		final String resourceContent = resource.getContentAsString();
		if (_uiOptions.getCustomStylesheets().isEmpty()) {
			resource.setContent(resourceContent.replace(Keys.GROUPDOCSVIEWERUI_STYLESHEETS_TARGET, "").getBytes(StandardCharsets.UTF_8));
		}

		final Stream<UiStylesheet> styleSheets = _uiOptions.getCustomStylesheets()
			.stream()
			.map(stylesheet -> UiStylesheet.create(_uiOptions, stylesheet));

		Stream<String> htmlStyles = styleSheets.map(uiStylesheet -> {
			String linkHref = StringExtensions.asRelativeResource(uiStylesheet.getResourceRelativePath());
			return "<link rel='stylesheet' href='" + linkHref + "'/>";
		});

		resource.setContent(resourceContent.replace(Keys.GROUPDOCSVIEWERUI_STYLESHEETS_TARGET,
				htmlStyles.collect(Collectors.joining("\n"))).getBytes(StandardCharsets.UTF_8));
	}

	private static class WindowConfig {

		@JsonProperty("apiEndpoint")
		public final String apiEndpoint;

		@JsonProperty("renderingMode")
		public final String renderingMode;

		@JsonProperty("staticContentMode")
		public final boolean staticContentMode;

		@JsonProperty("initialFile")
		public final String initialFile;

		@JsonProperty("preloadPages")
		public final int preloadPages;

		@JsonProperty("enableHeader")
		public final boolean enableHeader;

		@JsonProperty("enableToolbar")
		public final boolean enableToolbar;

		@JsonProperty("enablePageSelector")
		public final boolean enablePageSelector;

		@JsonProperty("enableDownloadPdf")
		public final boolean enableDownloadPdf;

		@JsonProperty("enableFileUpload")
		public final boolean enableFileUpload;

		@JsonProperty("enableFileBrowser")
		public final boolean enableFileBrowser;

		@JsonProperty("enableContextMenu")
		public final boolean enableContextMenu;

		@JsonProperty("enableZoom")
		public final boolean enableZoom;

		@JsonProperty("enableSearch")
		public final boolean enableSearch;

		@JsonProperty("enableFileName")
		public final boolean enableFileName;

		@JsonProperty("enableThumbnails")
		public final boolean enableThumbnails;

		@JsonProperty("enablePrint")
		public final boolean enablePrint;

		@JsonProperty("enablePresentation")
		public final boolean enablePresentation;

		@JsonProperty("enableHyperlinks")
		public final boolean enableHyperlinks;

		@JsonProperty("enableLanguageSelector")
		public final boolean enableLanguageSelector;

		@JsonProperty("defaultLanguage")
		public final String defaultLanguage;

		@JsonProperty("supportedLanguages")
		public final List<String> supportedLanguages;

		public <R> WindowConfig(String apiEndpoint, String renderingMode, boolean staticContentMode,
								String initialFile, int preloadPages, boolean enableHeader, boolean enableToolbar,
								boolean enablePageSelector, boolean enableDownloadPdf, boolean enableFileUpload,
								boolean enableFileBrowser, boolean enableContextMenu, boolean enableZoom,
								boolean enableSearch, boolean enableFileName, boolean enableThumbnails,
								boolean enablePrint, boolean enablePresentation, boolean enableHyperlinks,
								boolean enableLanguageSelector, String defaultLanguage, List<String> supportedLanguages) {
			this.apiEndpoint = apiEndpoint;
			this.renderingMode = renderingMode;
			this.staticContentMode = staticContentMode;
			this.initialFile = initialFile;
			this.preloadPages = preloadPages;
			this.enableHeader = enableHeader;
			this.enableToolbar = enableToolbar;
			this.enablePageSelector = enablePageSelector;
			this.enableDownloadPdf = enableDownloadPdf;
			this.enableFileUpload = enableFileUpload;
			this.enableFileBrowser = enableFileBrowser;
			this.enableContextMenu = enableContextMenu;
			this.enableZoom = enableZoom;
			this.enableSearch = enableSearch;
			this.enableFileName = enableFileName;
			this.enableThumbnails = enableThumbnails;
			this.enablePrint = enablePrint;
			this.enablePresentation = enablePresentation;
			this.enableHyperlinks = enableHyperlinks;
			this.enableLanguageSelector = enableLanguageSelector;
			this.defaultLanguage = defaultLanguage;
			this.supportedLanguages = supportedLanguages;
		}
	}
}
