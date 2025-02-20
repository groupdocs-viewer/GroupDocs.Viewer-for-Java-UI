package com.groupdocs.viewerui.ui.core.configuration;

public class Config {
	private boolean _enableLanguageSelector = true;
	private LanguageCode _defaultLanguage = LanguageCode.ENGLISH;
	private LanguageCode[] _supportedLanguages = LanguageCode.values();
	private RenderingMode _renderingMode = RenderingMode.Html;
	private boolean _staticContentMode = false;
	private String _initialFile;

	/* Control Visibility Settings */
	private int _preloadPages = 3;
	private boolean _enableContextMenu = true;
	private boolean _enableHyperlinks = true;
	private boolean _enableHeader = true;
	private boolean _enableToolbar = true;
	private boolean _enableFileName = true;
	private boolean _enableThumbnails = true;
	private boolean _enableZoom = true;
	private boolean _enablePageSelector = true;
	private boolean _enableSearch = true;
	private boolean _enablePrint = true;
	private boolean _enableDownloadPdf = true;

	/* Language and Localization Settings */
	private boolean _enablePresentation = true;
	private boolean _enableFileBrowser = true;
	private boolean _enableFileUpload = true;

	private String _baseUrl = "";

//	private String[] _supportedLanguages = new String[] { "ar", // ar - العربية
//			"ca", // ca-ES - Català
//			"cs", // cs-CZ - Čeština
//			"da", // da-DK - Dansk
//			"de", // de-DE - Deutsch
//			"el", // el-GR - Ελληνικά
//			"en", // en-US - English
//			"es", // es-ES - Español
//			"fil", // fil-PH - Filipino
//			"fr", // fr-FR - Français
//			"he", // he-IL - עברית
//			"hi", // hi-IN - हिन्दी
//			"id", // id-ID - Indonesia
//			"it", // it-IT - Italiano
//			"ja", // ja-JP - 日本語
//			"kk", // kk-KZ - Қазақ Тілі
//			"ko", // ko-KR - 한국어
//			"ms", // ms-MY - Melayu
//			"nl", // nl-NL - Nederlands
//			"pl", // pl-PL - Polski
//			"pt", // pt-PT - Português
//			"ro", // ro-RO - Română
//			"ru", // ru-RU - Русский
//			"sv", // sv-SE - Svenska
//			"vi", // vi-VN - Tiếng Việt
//			"th", // th-TH - ไทย
//			"tr", // tr-TR - Türkçe
//			"uk", // uk-UA - Українська
//			"zh-hans", // zh-Hans - 中文(简体)
//			"zh-hant", // zh-Hant" - 中文(繁體)
//	};

	/**
	 * Rendering mode for the UI.
	 * Possible values: "RenderingMode.Html", "RenderingMode.Image".
	 * Default value is "RenderingMode.Html".
	 */
	public RenderingMode getRenderingMode() {
		return _renderingMode;
	}

	public void setRenderingMode(RenderingMode renderingMode) {
		this._renderingMode = renderingMode;
	}

	/**
	 * When enabled app will use pre-generated static content via GET requests.
	 * Default value is false.
	 */
	public boolean isStaticContentMode() {
		return _staticContentMode;
	}

	public void setStaticContentMode(boolean staticContentMode) {
		this._staticContentMode = staticContentMode;
	}

	/**
	 * File to load by default
	 */
	public String getInitialFile() {
		return _initialFile;
	}

	public void setInitialFile(String initialFile) {
		this._initialFile = initialFile;
	}

	/**
	 * Number of pages to preload
	 */
	public int getPreloadPages() {
		return _preloadPages;
	}

	public void setPreloadPages(int preloadPages) {
		this._preloadPages = preloadPages;
	}

	/**
	 * Enable or disable right-click context menu
	 */
	public boolean isEnableContextMenu() {
		return _enableContextMenu;
	}

	public void setEnableContextMenu(boolean enableContextMenu) {
		this._enableContextMenu = enableContextMenu;
	}

	/**
	 * Enable or disable clickable links in documents
	 */
	public boolean isEnableHyperlinks() {
		return _enableHyperlinks;
	}

	public void setEnableHyperlinks(boolean enableHyperlinks) {
		this._enableHyperlinks = enableHyperlinks;
	}

	/**
	 * Show or hide header
	 */
	public boolean isEnableHeader() {
		return _enableHeader;
	}

	public void setEnableHeader(boolean enableHeader) {
		this._enableHeader = enableHeader;
	}

	/**
	 * Show or hide header
	 */
	public boolean isEnableToolbar() {
		return _enableToolbar;
	}

	public void setEnableToolbar(boolean enableToolbar) {
		this._enableToolbar = enableToolbar;
	}

	/**
	 * Show or hide filename
	 */
	public boolean isEnableFileName() {
		return _enableFileName;
	}

	public void setEnableFileName(boolean enableFileName) {
		this._enableFileName = enableFileName;
	}

	/**
	 * Show or hide thumbnails pane
	 */
	public boolean isEnableThumbnails() {
		return _enableThumbnails;
	}

	public void setEnableThumbnails(boolean enableThumbnails) {
		this._enableThumbnails = enableThumbnails;
	}

	/**
	 * Show or hide zoom controls
	 */
	public boolean isEnableZoom() {
		return _enableZoom;
	}

	public void setEnableZoom(boolean enableZoom) {
		this._enableZoom = enableZoom;
	}

	/**
	 * Show or hide page navigation menu
	 */
	public boolean isEnablePageSelector() {
		return _enablePageSelector;
	}

	public void setEnablePageSelector(boolean enablePageSelector) {
		this._enablePageSelector = enablePageSelector;
	}

	/**
	 * Show or hide search control
	 */
	public boolean isEnableSearch() {
		return _enableSearch;
	}

	public void setEnableSearch(boolean enableSearch) {
		this._enableSearch = enableSearch;
	}

	/**
	 * Show or hide "Print" button
	 */
	public boolean isEnablePrint() {
		return _enablePrint;
	}

	public void setEnablePrint(boolean enablePrint) {
		this._enablePrint = enablePrint;
	}

	/**
	 * Show or hide "Download PDF" button
	 */
	public boolean isEnableDownloadPdf() {
		return _enableDownloadPdf;
	}

	public void setEnableDownloadPdf(boolean enableDownloadPdf) {
		this._enableDownloadPdf = enableDownloadPdf;
	}

	/**
	 * Show or hide "Present" button
	 */
	public boolean isEnablePresentation() {
		return _enablePresentation;
	}

	public void setEnablePresentation(boolean enablePresentation) {
		this._enablePresentation = enablePresentation;
	}

	/**
	 * Show or hide "File Browser" button
	 */
	public boolean isEnableFileBrowser() {
		return _enableFileBrowser;
	}

	public void setEnableFileBrowser(boolean enableFileBrowser) {
		this._enableFileBrowser = enableFileBrowser;
	}

	/**
	 * Show or hide "Upload File" button
	 */
	public boolean isEnableFileUpload() {
		return _enableFileUpload;
	}

	public void setEnableFileUpload(boolean enableFileUpload) {
		this._enableFileUpload = enableFileUpload;
	}

	/**
	 * Show or hide language menu
	 */
	public boolean isEnableLanguageSelector() {
		return _enableLanguageSelector;
	}

	public void setEnableLanguageSelector(boolean enableLanguageSelector) {
		this._enableLanguageSelector = enableLanguageSelector;
	}

	/**
	 * Default language code. Default value is "en".
	 */
	public LanguageCode getDefaultLanguage() {
		return _defaultLanguage;
	}

	public void setDefaultLanguage(LanguageCode defaultLanguage) {
		this._defaultLanguage = defaultLanguage;
	}

	/**
	 * List of supported language codes.
	 */
	public LanguageCode[] getSupportedLanguages() {
		return _supportedLanguages;
	}

	public void setSupportedLanguages(LanguageCode[] supportedLanguages) {
		this._supportedLanguages = supportedLanguages;
	}

	public String getBaseUrl() {
		return _baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this._baseUrl = baseUrl;
	}
}
