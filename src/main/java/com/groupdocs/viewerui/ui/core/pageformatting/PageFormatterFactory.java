package com.groupdocs.viewerui.ui.core.pageformatting;

import com.groupdocs.viewerui.ui.core.PageFormatter;

public class PageFormatterFactory {
    private static PageFormatter _pageFormatter;

    public static synchronized PageFormatter getInstance() {
        if (_pageFormatter == null) {
            _pageFormatter = new NoopPageFormatter();
        }
        return _pageFormatter;
    }

    public static void setInstance(PageFormatter pageFormatter) {
        PageFormatterFactory._pageFormatter = pageFormatter;
    }
}
