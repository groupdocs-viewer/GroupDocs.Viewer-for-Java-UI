package com.groupdocs.viewerui.ui.core;

import com.groupdocs.viewerui.ui.core.configuration.Config;
import com.groupdocs.viewerui.ui.core.entities.ConfigEntry;

/**
 * ConfigEntryFactory is responsible for creating ConfigEntry objects based on the provided Config object.
 */
public class ConfigEntryFactory implements IConfigEntryFactory {

    @Override
    public ConfigEntry createConfigEntry(Config config) {
        if (config == null) {
            throw new IllegalArgumentException("config");
        }
        final ConfigEntry configEntry = new ConfigEntry();
        configEntry.setPageSelector(config.isPageSelector());
        configEntry.setDownload(config.isDownload());
        configEntry.setUpload(config.isUpload());
        configEntry.setPrint(config.isPrint());
        configEntry.setBrowse(config.isBrowse());
        configEntry.setRewrite(config.isRewrite());
        configEntry.setEnableRightClick(config.isEnableRightClick());
        configEntry.setDefaultDocument(config.getDefaultDocument());
        configEntry.setPreloadPageCount(config.getPreloadPageCount());
        configEntry.setZoom(config.isZoom());
        configEntry.setSearch(config.isSearch());
        configEntry.setThumbnails(config.isThumbnails());
        configEntry.setHtmlMode(config.isHtmlMode());
        configEntry.setPrintAllowed(config.isPrintAllowed());
        configEntry.setRotate(config.isRotate());
        configEntry.setSaveRotateState(config.isSaveRotateState());
        configEntry.setDefaultLanguage(config.getDefaultLanguage());
        configEntry.setSupportedLanguages(config.getSupportedLanguages());
        configEntry.setShowLanguageMenu(config.isShowLanguageMenu());
        configEntry.setShowToolBar(config.isShowToolBar());
        return configEntry;
    }
}
