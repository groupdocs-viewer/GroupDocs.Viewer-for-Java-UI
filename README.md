# GroupDocs.Viewer-for-Java-UI
UI - User Interface for GroupDocs.Viewer for Java. API for easily integrating a document viewer into 3rd-party projects.

## How to use with Spring

1. Create configuration class and configure Viewer

    ```java
    @Configuration
    class ViewerConfiguration {
    
        public static final String VIEWER_UI_PATH = "/viewer";
    
        public static final String VIEWER_CONFIG_ENDPOINT = "/viewer-config";
    
        public static final String VIEWER_API_ENDPOINT = "/viewer-api";
    
        private CommonEndpointMapper commonEndpointMapper;
    
        @PostConstruct
        public void init() {
            commonEndpointMapper = CommonEndpointMapper.setupGroupDocsViewer((viewerConfig, config) -> {
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
                    .setupLocalStorage(Paths.get("/home/user/files"));
        }
    
        @Bean
        public CommonEndpointMapper commonEndpointMapper() {
            return commonEndpointMapper;
        }
    }
    ```

2. Create a controller that will handle viewer requests

    ```java
    @Controller
    class ViewerController {
    
        private final CommonEndpointMapper _endpointMapper;
    
        public ViewerController(CommonEndpointMapper endpointMapper) {
            this._endpointMapper = endpointMapper;
        }
    
        @GetMapping({ViewerConfiguration.VIEWER_UI_PATH, ViewerConfiguration.VIEWER_UI_PATH + "/**",
            ViewerConfiguration.VIEWER_CONFIG_ENDPOINT, ViewerConfiguration.VIEWER_CONFIG_ENDPOINT + "/**"})
        public void handleViewerUiRequest(HttpServletRequest request, HttpServletResponse response) {
            handleViewerRequest(request, response);
        }
    
        @PostMapping({ViewerConfiguration.VIEWER_API_ENDPOINT, ViewerConfiguration.VIEWER_API_ENDPOINT + "/**"})
        public void handleViewerApiRequest(HttpServletRequest request, HttpServletResponse response) {
            handleViewerRequest(request, response);
        }
    
        private void handleViewerRequest(HttpServletRequest request, HttpServletResponse response) {
            try (final ServletInputStream inputStream = request.getInputStream();
                 final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream()) {
                final int resultCode = this._endpointMapper.handleViewerRequest(request.getRequestURI(), inputStream,
                    (headerName, headerValue) -> {
                        response.setHeader(headerName, headerValue);
                    }, arrayOutputStream);
                response.setStatus(resultCode); // Must be set before writing to output stream
                try (final ServletOutputStream outputStream = response.getOutputStream()) {
                    outputStream.write(arrayOutputStream.toByteArray());
                }
            } catch (IOException e) {
                // Handle exception
                throw new RuntimeException(e);
            }
        }
    }
    ```

3. Run application and open `http://127.0.0.1:8080/viewer` (`/viewer` part is from `VIEWER_UI_PATH`)