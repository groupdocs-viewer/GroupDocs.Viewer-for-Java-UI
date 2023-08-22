package com.groupdocs.viewerui.ui.core;

import com.groupdocs.viewerui.ui.core.configuration.Config;
import com.groupdocs.viewerui.ui.core.entities.ConfigEntry;

public interface IConfigEntryFactory {
    ConfigEntry createConfigEntry(Config config);
}
