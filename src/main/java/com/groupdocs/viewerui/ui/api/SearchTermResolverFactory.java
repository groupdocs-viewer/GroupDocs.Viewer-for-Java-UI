package com.groupdocs.viewerui.ui.api;

public class SearchTermResolverFactory {
    private static SearchTermResolver _searchTermResolver;

    public static synchronized SearchTermResolver getInstance() {
        if (_searchTermResolver == null) {
            _searchTermResolver = new DefaultSearchTermResolver();
        }
        return _searchTermResolver;
    }

    public static void setInstance(SearchTermResolver searchTermResolver) {
        SearchTermResolverFactory._searchTermResolver = searchTermResolver;
    }
}
