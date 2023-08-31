# GroupDocs.Viewer-for-Java-UI
User Interface for GroupDocs.Viewer for Java. API for easily integrating a document viewer into 3rd-party projects.

## How to use with Spring

1. Add required dependencies
   ```xml
    <dependencies>
      <dependency>
        <groupId>com.groupdocs</groupId>
        <artifactId>groupdocs-viewer-ui</artifactId>
        <version>23.7</version>
      </dependency>

      <dependency>
        <groupId>com.groupdocs</groupId>
        <artifactId>groupdocs-viewer</artifactId>
        <version>23.7</version>
      </dependency>
      <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.13.4.1</version>
      </dependency>
      <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-annotations</artifactId>
        <version>2.13.3</version>
      </dependency>
    </dependencies>
   ```

2. Configure GroupDocs.Viewer UI

    ```java
    @Configuration
    public class ViewerConfiguration {
        public static final String VIEWER_UI_PATH = "/viewer";
	
        public static final String VIEWER_CONFIG_ENDPOINT = "/viewer-config";
	
        public static final String VIEWER_API_ENDPOINT = "/viewer-api";
	
        private ServletsViewerEndpointHandler _viewerEndpointHandler;
	
        @PostConstruct
        public void init() {
            _viewerEndpointHandler = ServletsViewerEndpointHandler
                .setupGroupDocsViewer((viewerConfig, config) -> {
                    viewerConfig.setViewerType(ViewerType.PNG);
	
                    config.setPreloadPageCount(2);
                    config.setBaseUrl("http://127.0.0.1:8080");
                })
	
                .setupGroupDocsViewerUI(uiOptions -> {
                    uiOptions.setUiPath(VIEWER_UI_PATH);
                    uiOptions.setUiConfigEndpoint(VIEWER_CONFIG_ENDPOINT);
                })
                .setupGroupDocsViewerApi(apiOptions -> {
                    apiOptions.setApiEndpoint(VIEWER_API_ENDPOINT);
                })
                .setupLocalStorage(Paths.get("/home/liosha/workspace/groupdocs/files/"))
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
        public ServletsViewerEndpointHandler viewerEndpointHandler() {
            return _viewerEndpointHandler;
        }
    }
    ```

3. Create a controller that will handle viewer requests

   ```java
    @Controller
    public class ViewerController {
		private final JakartaViewerEndpointHandler _viewerEndpointHandler;
	
		public ViewerController(JakartaViewerEndpointHandler endpointHandler) {
			this._viewerEndpointHandler = endpointHandler;
		}
	
		@GetMapping({ ViewerConfiguration.VIEWER_UI_PATH, ViewerConfiguration.VIEWER_UI_PATH + "/**",
				ViewerConfiguration.VIEWER_CONFIG_ENDPOINT, ViewerConfiguration.VIEWER_CONFIG_ENDPOINT + "/**",
				ViewerConfiguration.VIEWER_API_ENDPOINT, ViewerConfiguration.VIEWER_API_ENDPOINT + "/**" })
		public void handleViewerUiRequest(HttpServletRequest request, HttpServletResponse response) {
			this._viewerEndpointHandler.handleViewerRequest(request, response);
		}
	
		@PostMapping({ ViewerConfiguration.VIEWER_API_ENDPOINT, ViewerConfiguration.VIEWER_API_ENDPOINT + "/**" })
		public void handleViewerApiRequest(HttpServletRequest request, HttpServletResponse response) {
			this._viewerEndpointHandler.handleViewerRequest(request, response);
		}
    }
    ```

3. Run application and open `http://127.0.0.1:8080/viewer` (`/viewer` part is from `VIEWER_UI_PATH`)