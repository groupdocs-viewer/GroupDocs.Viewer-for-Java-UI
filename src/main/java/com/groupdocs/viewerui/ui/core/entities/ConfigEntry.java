package com.groupdocs.viewerui.ui.core.entities;

import com.groupdocs.viewerui.ui.core.configuration.LanguageCode;
import com.groupdocs.viewerui.ui.core.configuration.RenderingMode;

public class ConfigEntry {
	private boolean enableLanguageSelector = true;
	private LanguageCode defaultLanguage = LanguageCode.ENGLISH;
	private LanguageCode[] supportedLanguages = LanguageCode.values();
	private RenderingMode renderingMode = RenderingMode.Html;
	private boolean staticContentMode = false;
	private String initialFile;
	private int preloadPages = 3;
	private boolean enableContextMenu = true;
	private boolean enableHyperlinks = true;
	private boolean enableHeader = true;
	private boolean enableToolbar = true;
	private boolean enableFileName = true;
	private boolean enableThumbnails = true;
	private boolean enableZoom = true;
	private boolean enablePageSelector = true;
	private boolean enableSearch = true;
	private boolean enablePrint = true;
	private boolean enableDownloadPdf = true;
	private boolean enablePresentation = true;
	private boolean enableFileBrowser = true;
	private boolean enableFileUpload = true;
	private String baseUrl = "";

	public boolean isEnableLanguageSelector() {
		return enableLanguageSelector;
	}

	public void setEnableLanguageSelector(boolean enableLanguageSelector) {
		this.enableLanguageSelector = enableLanguageSelector;
	}

	public LanguageCode getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(LanguageCode defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public LanguageCode[] getSupportedLanguages() {
		return supportedLanguages;
	}

	public void setSupportedLanguages(LanguageCode[] supportedLanguages) {
		this.supportedLanguages = supportedLanguages;
	}

	public RenderingMode getRenderingMode() {
		return renderingMode;
	}

	public void setRenderingMode(RenderingMode renderingMode) {
		this.renderingMode = renderingMode;
	}

	public boolean isStaticContentMode() {
		return staticContentMode;
	}

	public void setStaticContentMode(boolean staticContentMode) {
		this.staticContentMode = staticContentMode;
	}

	public String getInitialFile() {
		return initialFile;
	}

	public void setInitialFile(String initialFile) {
		this.initialFile = initialFile;
	}

	public int getPreloadPages() {
		return preloadPages;
	}

	public void setPreloadPages(int preloadPages) {
		this.preloadPages = preloadPages;
	}

	public boolean isEnableContextMenu() {
		return enableContextMenu;
	}

	public void setEnableContextMenu(boolean enableContextMenu) {
		this.enableContextMenu = enableContextMenu;
	}

	public boolean isEnableHyperlinks() {
		return enableHyperlinks;
	}

	public void setEnableHyperlinks(boolean enableHyperlinks) {
		this.enableHyperlinks = enableHyperlinks;
	}

	public boolean isEnableHeader() {
		return enableHeader;
	}

	public void setEnableHeader(boolean enableHeader) {
		this.enableHeader = enableHeader;
	}

	public boolean isEnableToolbar() {
		return enableToolbar;
	}

	public void setEnableToolbar(boolean enableToolbar) {
		this.enableToolbar = enableToolbar;
	}

	public boolean isEnableFileName() {
		return enableFileName;
	}

	public void setEnableFileName(boolean enableFileName) {
		this.enableFileName = enableFileName;
	}

	public boolean isEnableThumbnails() {
		return enableThumbnails;
	}

	public void setEnableThumbnails(boolean enableThumbnails) {
		this.enableThumbnails = enableThumbnails;
	}

	public boolean isEnableZoom() {
		return enableZoom;
	}

	public void setEnableZoom(boolean enableZoom) {
		this.enableZoom = enableZoom;
	}

	public boolean isEnablePageSelector() {
		return enablePageSelector;
	}

	public void setEnablePageSelector(boolean enablePageSelector) {
		this.enablePageSelector = enablePageSelector;
	}

	public boolean isEnableSearch() {
		return enableSearch;
	}

	public void setEnableSearch(boolean enableSearch) {
		this.enableSearch = enableSearch;
	}

	public boolean isEnablePrint() {
		return enablePrint;
	}

	public void setEnablePrint(boolean enablePrint) {
		this.enablePrint = enablePrint;
	}

	public boolean isEnableDownloadPdf() {
		return enableDownloadPdf;
	}

	public void setEnableDownloadPdf(boolean enableDownloadPdf) {
		this.enableDownloadPdf = enableDownloadPdf;
	}

	public boolean isEnablePresentation() {
		return enablePresentation;
	}

	public void setEnablePresentation(boolean enablePresentation) {
		this.enablePresentation = enablePresentation;
	}

	public boolean isEnableFileBrowser() {
		return enableFileBrowser;
	}

	public void setEnableFileBrowser(boolean enableFileBrowser) {
		this.enableFileBrowser = enableFileBrowser;
	}

	public boolean isEnableFileUpload() {
		return enableFileUpload;
	}

	public void setEnableFileUpload(boolean enableFileUpload) {
		this.enableFileUpload = enableFileUpload;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
}
