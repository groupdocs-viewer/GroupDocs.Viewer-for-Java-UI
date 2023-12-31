package com.example.groupdocsviewerjavauispringboot;

import com.groupdocs.viewerui.handler.JakartaViewerEndpointHandler;
import com.groupdocs.viewerui.ui.core.ViewerType;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration
public class ViewerConfiguration {
    public static final String VIEWER_UI_PATH = "/viewer";

    public static final String VIEWER_CONFIG_ENDPOINT = "/viewer-config";

    public static final String VIEWER_API_ENDPOINT = "/viewer-api";

    private JakartaViewerEndpointHandler _viewerEndpointHandler;

    @PostConstruct
    public void init() {
        _viewerEndpointHandler = JakartaViewerEndpointHandler
                .setupGroupDocsViewer((viewerConfig, config) -> {
                    viewerConfig.setViewerType(ViewerType.PNG);

                    config.setPreloadPageCount(2);
                    config.setBaseUrl("http://localhost:8080");
                })

                .setupGroupDocsViewerUI(uiOptions -> {
                    uiOptions.setUiPath(VIEWER_UI_PATH);
                    uiOptions.setUiConfigEndpoint(VIEWER_CONFIG_ENDPOINT);
                })
                .setupGroupDocsViewerApi(apiOptions -> {
                    apiOptions.setApiEndpoint(VIEWER_API_ENDPOINT);
                })
                .setupLocalStorage(Paths.get("./files").toAbsolutePath())
                .setupInMemoryCache(inMemoryCacheConfig -> {
                    inMemoryCacheConfig.setGroupCacheEntriesByFile(false);
                    inMemoryCacheConfig.setCacheEntryExpirationTimeoutMinutes(3);
                })
        //			.setupLocalCache(cacheConfig -> {
        //				cacheConfig.setCachePath(Paths.get("/home/liosha/workspace/groupdocs/files/cache"));
        //			})
        ;
    }

    @Bean
    public JakartaViewerEndpointHandler viewerEndpointHandler() {
        return _viewerEndpointHandler;
    }
}
