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
        configEntry.setEnableLanguageSelector(config.isEnableLanguageSelector());
        configEntry.setDefaultLanguage(config.getDefaultLanguage());
        configEntry.setSupportedLanguages(config.getSupportedLanguages());
        configEntry.setRenderingMode(config.getRenderingMode());
        configEntry.setStaticContentMode(config.isStaticContentMode());
        configEntry.setInitialFile(config.getInitialFile());
        configEntry.setPreloadPages(config.getPreloadPages());
        configEntry.setEnableContextMenu(config.isEnableContextMenu());
        configEntry.setEnableHyperlinks(config.isEnableHyperlinks());
        configEntry.setEnableHeader(config.isEnableHeader());
        configEntry.setEnableToolbar(config.isEnableToolbar());
        configEntry.setEnableFileName(config.isEnableFileName());
        configEntry.setEnableThumbnails(config.isEnableThumbnails());
        configEntry.setEnableZoom(config.isEnableZoom());
        configEntry.setEnablePageSelector(config.isEnablePageSelector());
        configEntry.setEnableSearch(config.isEnableSearch());
        configEntry.setEnablePrint(config.isEnablePrint());
        configEntry.setEnableDownloadPdf(config.isEnableDownloadPdf());
        configEntry.setEnablePresentation(config.isEnablePresentation());
        configEntry.setEnableFileBrowser(config.isEnableFileBrowser());
        configEntry.setEnableFileUpload(config.isEnableFileUpload());

        return configEntry;
    }
}
