package com.groupdocs.viewerui.ui.api.models;

public class LoadConfigResponse {

	// [JsonPropertyName("pageSelector")]
	private boolean _pageSelector;

	// [JsonPropertyName("download")]
	private boolean _download;

	// [JsonPropertyName("upload")]
	private boolean _upload;

	// [JsonPropertyName("print")]
	private boolean _print;

	// [JsonPropertyName("browse")]
	private boolean _browse;

	// [JsonPropertyName("rewrite")]
	private boolean _rewrite;

	// [JsonPropertyName("enableRightClick")]
	private boolean _enableRightClick;

	// [JsonPropertyName("defaultDocument")]
	private String _defaultDocument;

	// [JsonPropertyName("preloadPageCount")]
	private int _preloadPageCount;

	// [JsonPropertyName("zoom")]
	private boolean _zoom;

	// [JsonPropertyName("search")]
	private boolean _search;

	// [JsonPropertyName("thumbnails")]
	private boolean _thumbnails;

	// [JsonPropertyName("htmlMode")]
	private boolean _htmlMode;

	// [JsonPropertyName("printAllowed")]
	private boolean _printAllowed;

	// [JsonPropertyName("rotate")]
	private boolean _rotate;

	// [JsonPropertyName("saveRotateState")]
	private boolean _saveRotateState;

	// [JsonPropertyName("defaultLanguage")]
	private String _defaultLanguage;

	// [JsonPropertyName("supportedLanguages")]
	private String[] _supportedLanguages;

	// [JsonPropertyName("showLanguageMenu")]
	private boolean _showLanguageMenu;

	// [JsonPropertyName("showToolBar")]
	private boolean _showToolBar;

	/**
	 * Enables page selector control.
	 */
	public boolean isPageSelector() {
		return _pageSelector;
	}

	public void setPageSelector(boolean pageSelector) {
		this._pageSelector = pageSelector;
	}

	/**
	 * Enables download button.
	 */
	public boolean isDownload() {
		return _download;
	}

	public void setDownload(boolean download) {
		this._download = download;
	}

	/**
	 * Enables upload.
	 */
	public boolean isUpload() {
		return _upload;
	}

	public void setUpload(boolean upload) {
		this._upload = upload;
	}

	/**
	 * Enables printing.
	 */
	public boolean isPrint() {
		return _print;
	}

	public void setPrint(boolean print) {
		this._print = print;
	}

	/**
	 * Enables file browser.
	 */
	public boolean isBrowse() {
		return _browse;
	}

	public void setBrowse(boolean browse) {
		this._browse = browse;
	}

	/**
	 * Enables file rewrite.
	 */
	public boolean isRewrite() {
		return _rewrite;
	}

	public void setRewrite(boolean rewrite) {
		this._rewrite = rewrite;
	}

	/**
	 * Enables right click.
	 */
	public boolean isEnableRightclick() {
		return _enableRightClick;
	}

	public void setEnableRightClick(boolean enableRightClick) {
		this._enableRightClick = enableRightClick;
	}

	/**
	 * The default document to view.
	 */
	public String getDefaultDocument() {
		return _defaultDocument;
	}

	public void setDefaultDocument(String defaultDocument) {
		this._defaultDocument = defaultDocument;
	}

	/**
	 * Count pages to preload.
	 */
	public int getPreloadPagecount() {
		return _preloadPageCount;
	}

	public void setPreloadPageCount(int preloadPageCount) {
		this._preloadPageCount = preloadPageCount;
	}

	/**
	 * Enables zoom.
	 */
	public boolean isZoom() {
		return _zoom;
	}

	public void setZoom(boolean zoom) {
		this._zoom = zoom;
	}

	/**
	 * Enables searching.
	 */
	public boolean isSearch() {
		return _search;
	}

	public void setSearch(boolean search) {
		this._search = search;
	}

	/**
	 * Enables thumbnails.
	 */
	public boolean isThumbnails() {
		return _thumbnails;
	}

	public void setThumbnails(boolean thumbnails) {
		this._thumbnails = thumbnails;
	}

	/**
	 * Image or HTML mode.
	 */
	public boolean isHtmlMode() {
		return _htmlMode;
	}

	public void setHtmlMode(boolean htmlMode) {
		this._htmlMode = htmlMode;
	}

	/**
	 * Enables printing
	 */
	public boolean isPrintAllowed() {
		return _printAllowed;
	}

	public void setPrintAllowed(boolean printAllowed) {
		this._printAllowed = printAllowed;
	}

	/**
	 * Enables rotation
	 */
	public boolean isRotate() {
		return _rotate;
	}

	public void setRotate(boolean rotate) {
		this._rotate = rotate;
	}

	/**
	 * Enables saving of rotation state
	 */
	public boolean isSaveRotateState() {
		return _saveRotateState;
	}

	public void setSaveRotateState(boolean saveRotateState) {
		this._saveRotateState = saveRotateState;
	}

	/**
	 * Default language e.g. "en".
	 */
	public String getDefaultLanguage() {
		return _defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this._defaultLanguage = defaultLanguage;
	}

	/**
	 * Supported languages e.g. [ "en", "fr", "de" ]
	 */
	public String[] getSupportedLanguages() {
		return _supportedLanguages;
	}

	public void setSupportedLanguages(String[] supportedLanguages) {
		this._supportedLanguages = supportedLanguages;
	}

	/**
	 * Enables language menu.
	 */
	public boolean isShowLanguageMenu() {
		return _showLanguageMenu;
	}

	public void setShowLanguageMenu(boolean showLanguageMenu) {
		this._showLanguageMenu = showLanguageMenu;
	}

	/**
	 * Top toolbar show flag
	 */
	public boolean isShowToolBar() {
		return _showToolBar;
	}

	public void setShowToolBar(boolean showToolBar) {
		this._showToolBar = showToolBar;
	}

}
