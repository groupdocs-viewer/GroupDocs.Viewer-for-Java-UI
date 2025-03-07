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

    public static final String VIEWER_API_ENDPOINT = "/viewer-api";

    private JakartaViewerEndpointHandler _viewerEndpointHandler;

    @PostConstruct
    public void init() {
        _viewerEndpointHandler = JakartaViewerEndpointHandler
                .setupGroupDocsViewer((viewerConfig, config) -> {
                    final ViewerType viewerType = ViewerType.PNG;

                   viewerConfig.setViewerType(viewerType);
//                    viewerConfig.setLicensePath("GroupDocs.Viewer.Product.Family.lic");

                    config.setPreloadPages(2);
                    config.setBaseUrl("http://localhost:8080");
                    config.setRenderingMode(viewerType.toRenderingMode());
                })

                .setupGroupDocsViewerUI(uiOptions -> {
                    uiOptions.setUiPath(VIEWER_UI_PATH);
                })
                .setupGroupDocsViewerApi(apiOptions -> {
                    apiOptions.setApiEndpoint(VIEWER_API_ENDPOINT);
                })
//                .setupAwsS3Storage(awsS3Options -> {
//                    awsS3Options.setAccessKey("AccessKey");
//                    awsS3Options.setSecretKey("SecretKey");
//                    awsS3Options.setRegion("us-east-1");
//                    awsS3Options.setBucketName("BucketName");
//                })
//                .setupAzureBlobStorage(azureBlobOptions -> {
//                    azureBlobOptions.setAccountName("AccountName");
//                    azureBlobOptions.setAccountKey("AccountKey");
//                    azureBlobOptions.setContainerName("ContainerName");
//                })
                .setupLocalStorage(Paths.get("./files").toAbsolutePath())
                .setupInMemoryCache(inMemoryCacheConfig -> {
                    inMemoryCacheConfig.setGroupCacheEntriesByFile(false);
                    inMemoryCacheConfig.setCacheEntryExpirationTimeoutMinutes(3);
                })
//                .setupAwsS3Cache(awsS3Options -> {
//                    awsS3Options.setAccessKey("AccessKey");
//                    awsS3Options.setSecretKey("SecretKey");
//                    awsS3Options.setRegion("us-east-1");
//                    awsS3Options.setBucketName("BucketName");
//                });
//                .setupAzureBlobStorageCache(azureBlobOptions -> {
//                    azureBlobOptions.setAccountName("AccountName");
//                    azureBlobOptions.setAccountKey("AccountKey");
//                    azureBlobOptions.setContainerName("ContainerName");
//                });
//                .setupLocalCache(cacheConfig -> {
//                    cacheConfig.setCachePath(Paths.get("/home/liosha/workspace/groupdocs/files/cache"));
//                })
        ;
    }

    @Bean
    public JakartaViewerEndpointHandler viewerEndpointHandler() {
        return _viewerEndpointHandler;
    }
}
