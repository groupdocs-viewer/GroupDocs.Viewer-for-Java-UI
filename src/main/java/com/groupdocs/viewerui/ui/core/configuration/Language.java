package com.groupdocs.viewerui.ui.core.configuration;

public enum Language {

	ARABIC("ar"), CATALAN("ca"), CZECH("cs"), DANISH("da"), GERMAN("de"), GREEK("el"), ENGLISH("en"), SPANISH("es"),
	FILIPINO("fil"), FRENCH("fr"), HEBREW("he"), HINDI("hi"), INDONESIAN("id"), ITALIAN("it"), JAPANESE("ja"),
	KAZAKH("kk"), KOREAN("ko"), MALAY("ms"), DUTCH("nl"), POLISH("pl"), PORTUGUESE("pt"), ROMANIAN("ro"), RUSSIAN("ru"),
	SWEDISH("sv"), VIETNAMESE("vi"), THAI("th"), TURKISH("tr"), UKRAINIAN("uk"), CHINESE_SIMPLIFIED("zh-hans"),
	CHINESE_TRADITIONAL("zh-hant");

	private final String _code;

	Language(String code) {
		_code = code;
	}

	public String getCode() {
		return _code;
	}

}
