package com.groupdocs.viewerui.ui.core.configuration;

import com.groupdocs.viewerui.ui.core.ViewerType;

import java.util.Arrays;

public class Config {

	// Client-side config
	private String _defaultDocument = "";

	private int _preloadPageCount = 3;

	private boolean _isPageSelector = true;

	private boolean _isThumbnails = true;

	private boolean _isZoom = true;

	private boolean _isSearch = true;

	private boolean _isShowToolBar = true;

	private boolean _isEnableRightClick = true;

	// Client-side and server-side config
	private String _baseUrl = "";

	private boolean _isDownload = true;

	private boolean _isUpload = true;

	private boolean _isRewrite = false;

	private boolean _isPrint = true;

	private boolean _isBrowse = true;

	private boolean _isPrintAllowed = true;

	private boolean _isHtmlMode = true;

	// I18n
	private boolean _isShowLanguageMenu = true;

	private String _defaultLanguage = "en";

	private String[] _supportedLanguages = new String[] { "ar", // ar - العربية
			"ca", // ca-ES - Català
			"cs", // cs-CZ - Čeština
			"da", // da-DK - Dansk
			"de", // de-DE - Deutsch
			"el", // el-GR - Ελληνικά
			"en", // en-US - English
			"es", // es-ES - Español
			"fil", // fil-PH - Filipino
			"fr", // fr-FR - Français
			"he", // he-IL - עברית
			"hi", // hi-IN - हिन्दी
			"id", // id-ID - Indonesia
			"it", // it-IT - Italiano
			"ja", // ja-JP - 日本語
			"kk", // kk-KZ - Қазақ Тілі
			"ko", // ko-KR - 한국어
			"ms", // ms-MY - Melayu
			"nl", // nl-NL - Nederlands
			"pl", // pl-PL - Polski
			"pt", // pt-PT - Português
			"ro", // ro-RO - Română
			"ru", // ru-RU - Русский
			"sv", // sv-SE - Svenska
			"vi", // vi-VN - Tiếng Việt
			"th", // th-TH - ไทย
			"tr", // tr-TR - Türkçe
			"uk", // uk-UA - Українська
			"zh-hans", // zh-Hans - 中文(简体)
			"zh-hant", // zh-Hant" - 中文(繁體)
	};

	private boolean _isRotate = false;

	private boolean _isSaveRotateState = false;

	public String getDefaultDocument() {
		return _defaultDocument;
	}

	public Config setDefaultDocument(String filePath) {
		_defaultDocument = filePath;
		return this;
	}

	public int getPreloadPageCount() {
		return _preloadPageCount;
	}

	public boolean isPageSelector() {
		return _isPageSelector;
	}

	public boolean isThumbnails() {
		return _isThumbnails;
	}

	public boolean isZoom() {
		return _isZoom;
	}

	public boolean isSearch() {
		return _isSearch;
	}

	public boolean isShowToolBar() {
		return _isShowToolBar;
	}

	public boolean isEnableRightClick() {
		return _isEnableRightClick;
	}

	public String getBaseUrl() {
		return _baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this._baseUrl = baseUrl;
	}

	public boolean isDownload() {
		return _isDownload;
	}

	public boolean isUpload() {
		return _isUpload;
	}

	public boolean isRewrite() {
		return _isRewrite;
	}

	public boolean isPrint() {
		return _isPrint;
	}

	public boolean isBrowse() {
		return _isBrowse;
	}

	public boolean isPrintAllowed() {
		return _isPrintAllowed;
	}

	public boolean isHtmlMode() {
		return _isHtmlMode;
	}

	public boolean isShowLanguageMenu() {
		return _isShowLanguageMenu;
	}

	public String getDefaultLanguage() {
		return _defaultLanguage;
	}

	/// <summary>
	/// Sets default language out of supported:
	/// <see cref="Language.Arabic"/>,
	/// <see cref="Language.Catalan"/>,
	/// <see cref="Language.Czech"/>,
	/// <see cref="Language.Danish"/>,
	/// <see cref="Language.German"/>,
	/// <see cref="Language.Greek"/>,
	/// <see cref="Language.English"/>,
	/// <see cref="Language.Spanish"/>,
	/// <see cref="Language.Filipino"/>,
	/// <see cref="Language.French"/>,
	/// <see cref="Language.Hebrew"/>,
	/// <see cref="Language.Hindi"/>,
	/// <see cref="Language.Indonesian"/>,
	/// <see cref="Language.Italian"/>,
	/// <see cref="Language.Japanese"/>,
	/// <see cref="Language.Kazakh"/>,
	/// <see cref="Language.Korean"/>,
	/// <see cref="Language.Malay"/>,
	/// <see cref="Language.Dutch"/>,
	/// <see cref="Language.Polish"/>,
	/// <see cref="Language.Portuguese"/>,
	/// <see cref="Language.Romanian"/>,
	/// <see cref="Language.Russian"/>,
	/// <see cref="Language.Swedish"/>,
	/// <see cref="Language.Vietnamese"/>,
	/// <see cref="Language.Thai"/>,
	/// <see cref="Language.Turkish"/>,
	/// <see cref="Language.Ukrainian"/>,
	/// <see cref="Language.ChineseSimplified"/>,
	/// <see cref="Language.ChineseTraditional"/>
	/// </summary>
	/// <param name="language">Default language e.g. <see
	/// cref="Language.English"/>.</param>
	/// <returns>This config instance.</returns>
	public Config setDefaultLanguage(Language language) {
		_defaultLanguage = language.getCode();
		return this;
	}

	public String[] getSupportedLanguages() {
		return _supportedLanguages;
	}

	public boolean isRotate() {
		return _isRotate;
	}

	public boolean isSaveRotateState() {
		return _isSaveRotateState;
	}

	public void setSupportedLanguages(String[] supportedLanguages) {
		this._supportedLanguages = supportedLanguages;
	}

	/// <summary>
	/// Set supported UI languages. The following languages are supported:
	/// <see cref="Language.Arabic"/>,
	/// <see cref="Language.Catalan"/>,
	/// <see cref="Language.Czech"/>,
	/// <see cref="Language.Danish"/>,
	/// <see cref="Language.German"/>,
	/// <see cref="Language.Greek"/>,
	/// <see cref="Language.English"/>,
	/// <see cref="Language.Spanish"/>,
	/// <see cref="Language.Filipino"/>,
	/// <see cref="Language.French"/>,
	/// <see cref="Language.Hebrew"/>,
	/// <see cref="Language.Hindi"/>,
	/// <see cref="Language.Indonesian"/>,
	/// <see cref="Language.Italian"/>,
	/// <see cref="Language.Japanese"/>,
	/// <see cref="Language.Kazakh"/>,
	/// <see cref="Language.Korean"/>,
	/// <see cref="Language.Malay"/>,
	/// <see cref="Language.Dutch"/>,
	/// <see cref="Language.Polish"/>,
	/// <see cref="Language.Portuguese"/>,
	/// <see cref="Language.Romanian"/>,
	/// <see cref="Language.Russian"/>,
	/// <see cref="Language.Swedish"/>,
	/// <see cref="Language.Vietnamese"/>,
	/// <see cref="Language.Thai"/>,
	/// <see cref="Language.Turkish"/>,
	/// <see cref="Language.Ukrainian"/>,
	/// <see cref="Language.ChineseSimplified"/>,
	/// <see cref="Language.ChineseTraditional"/>
	/// </summary>
	/// <param name="languages">Supported languages.</param>
	/// <returns>This config instance.</returns>
	public Config setSupportedLanguages(Language... languages) {
		setSupportedLanguages(Arrays.stream(languages).map(Language::getCode).toArray(String[]::new));
		return this;
	}

	public Config setViewerType(ViewerType viewerType) {
		_isHtmlMode = viewerType == ViewerType.HTML_WITH_EXTERNAL_RESOURCES
				|| viewerType == ViewerType.HTML_WITH_EMBEDDED_RESOURCES;
		return this;
	}

	public Config setPreloadPageCount(int countPages) {
		_preloadPageCount = countPages;
		return this;
	}

	public Config hidePageSelectorControl() {
		_isPageSelector = false;
		return this;
	}

	public Config showPageSelectorControl() {
		_isPageSelector = true;
		return this;
	}

	public Config hideThumbnailsControl() {
		_isThumbnails = false;
		return this;
	}

	public Config showThumbnailsControl() {
		_isThumbnails = true;
		return this;
	}

	public Config disableFileDownload() {
		_isDownload = false;
		return this;
	}

	public Config enableFileDownload() {
		_isDownload = true;
		return this;
	}

	public Config disableFileUpload() {
		_isUpload = false;
		return this;
	}

	public Config enableFileUpload() {
		_isUpload = true;
		return this;
	}

	public Config rewriteFilesOnUpload() {
		_isRewrite = true;
		return this;
	}

	public Config disablePrint() {
		_isPrint = false;
		_isPrintAllowed = false;
		return this;
	}

	public Config enablePrint() {
		_isPrint = true;
		_isPrintAllowed = true;
		return this;
	}

	public Config disableFileBrowsing() {
		_isBrowse = false;
		return this;
	}

	public Config enableFileBrowsing() {
		_isBrowse = true;
		return this;
	}

	public Config hideZoomButton() {
		_isZoom = false;
		return this;
	}

	public Config showZoomButton() {
		_isZoom = true;
		return this;
	}

	public Config hideSearchControl() {
		_isSearch = false;
		return this;
	}

	public Config showSearchControl() {
		_isSearch = true;
		return this;
	}

	public Config hideToolBar() {
		_isShowToolBar = false;
		return this;
	}

	public Config showToolBar() {
		_isShowToolBar = true;
		return this;
	}

	public Config hidePageRotationControl() {
		_isRotate = false;
		return this;
	}

	public Config showPageRotationControl() {
		_isRotate = true;
		return this;
	}

	public Config disableRightClick() {
		_isEnableRightClick = false;
		return this;
	}

	public Config enableRightClick() {
		_isEnableRightClick = true;
		return this;
	}

	public Config hideLanguageMenu() {
		_isShowLanguageMenu = false;
		return this;
	}

	public Config showLanguageMenu() {
		_isShowLanguageMenu = true;
		return this;
	}

	@Override
	public String toString() {
		return "Config {" +
			   " defaultDocument='" + _defaultDocument + '\'' +
			   ", preloadPageCount=" + _preloadPageCount +
			   ", isPageSelector=" + _isPageSelector +
			   ", isThumbnails=" + _isThumbnails +
			   ", isZoom=" + _isZoom +
			   ", isSearch=" + _isSearch +
			   ", isShowToolBar=" + _isShowToolBar +
			   ", isEnableRightClick=" + _isEnableRightClick +
			   ", baseUrl='" + _baseUrl + '\'' +
			   ", isDownload=" + _isDownload +
			   ", isUpload=" + _isUpload +
			   ", isRewrite=" + _isRewrite +
			   ", isPrint=" + _isPrint +
			   ", isBrowse=" + _isBrowse +
			   ", isPrintAllowed=" + _isPrintAllowed +
			   ", isHtmlMode=" + _isHtmlMode +
			   ", isShowLanguageMenu=" + _isShowLanguageMenu +
			   ", defaultLanguage='" + _defaultLanguage + '\'' +
			   ", supportedLanguages=" + Arrays.toString(_supportedLanguages) +
			   ", isRotate=" + _isRotate +
			   ", isSaveRotateState=" + _isSaveRotateState +
			   " }";
	}
}
