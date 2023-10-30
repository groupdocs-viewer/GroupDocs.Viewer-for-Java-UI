# GroupDocs.Viewer-for-Java-UI
User Interface for GroupDocs.Viewer for Java. API for easily integrating a document viewer into 3rd-party projects.

## How to use with Spring

1. Add GroupDocs Maven repository

   ```xml
       <repositories>
           <repository>
               <id>releases.groupdocs.com</id>
               <name>releases.groupdocs</name>
               <url>https://releases.groupdocs.com/java/repo/</url>
           </repository>
       </repositories>
   ```

2. Add required dependencies
   ```xml
    <dependencies>
      <dependency>
        <groupId>com.groupdocs</groupId>
        <artifactId>groupdocs-viewer-ui</artifactId>
        <version>23.7.1</version>
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

3. Configure GroupDocs.Viewer UI

    ```java
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
                       config.setBaseUrl("http://127.0.0.1:8080");
                   })
   
                   .setupGroupDocsViewerUI(uiOptions -> {
                       uiOptions.setUiPath(VIEWER_UI_PATH);
                       uiOptions.setUiConfigEndpoint(VIEWER_CONFIG_ENDPOINT);
                   })
                   .setupGroupDocsViewerApi(apiOptions -> {
                       apiOptions.setApiEndpoint(VIEWER_API_ENDPOINT);
                   })
                   .setupLocalStorage(Paths.get("./").toAbsolutePath())
                   .setupInMemoryCache(inMemoryCacheConfig -> {
                       inMemoryCacheConfig.setGroupCacheEntriesByFile(false);
                       inMemoryCacheConfig.setCacheEntryExpirationTimeoutMinutes(3);
                   })
       //			.setupLocalCache(cacheConfig -> {
       //				cacheConfig.setCachePath(Paths.get("/home/user/cache"));
       //			})
           ;
       }
   
       @Bean
       public JakartaViewerEndpointHandler viewerEndpointHandler() {
           return _viewerEndpointHandler;
       }
   }
    ```

4. Create a controller that will handle viewer requests

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

5. Run application and open `http://127.0.0.1:8080/viewer` (`/viewer` part is from `VIEWER_UI_PATH`)

Optionally you can enable logging by adding `logback.xml` file to `src/main/resources` directory. Example of the file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

	<property name="LOGS" value="./logs" />

	<appender name="Console"
			  class="ch.qos.logback.core.ConsoleAppender">
		<layout class="ch.qos.logback.classic.PatternLayout">
			<Pattern>
				%black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1}): %msg%n%throwable
			</Pattern>
		</layout>
	</appender>

	<appender name="RollingFile"
			  class="ch.qos.logback.core.rolling.RollingFileAppender">
		<file>${LOGS}/spring-boot-logger.log</file>
		<encoder
			class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
			<Pattern>%d %p %C{1} [%t] %m%n</Pattern>
		</encoder>

		<rollingPolicy
			class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
			<!-- rollover daily and when the file reaches 10 MegaBytes -->
			<fileNamePattern>${LOGS}/archived/spring-boot-logger-%d{yyyy-MM-dd}.%i.log
			</fileNamePattern>
			<timeBasedFileNamingAndTriggeringPolicy
				class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
				<maxFileSize>10MB</maxFileSize>
			</timeBasedFileNamingAndTriggeringPolicy>
		</rollingPolicy>
	</appender>

	<!-- LOG everything at INFO level -->
	<root level="info">
		<appender-ref ref="RollingFile" />
		<appender-ref ref="Console" />
	</root>

	<!-- LOG "com.baeldung*" at TRACE level -->
	<logger name="com.groupdocs" level="debug" additivity="false">
		<appender-ref ref="RollingFile" />
		<appender-ref ref="Console" />
	</logger>

</configuration>
```

## Developer notes

In case of getting next error

```shell
error: error reading /home/jenkins/groupdocs-viewer.jar; Unsupported size: 19732345 for JarEntry META-INF/MANIFEST.MF. Allowed max size: 8000000 bytes
```

Create environment variables `MAVEN_OPTS`, `JAVA_OPTS` or `JAVA_TOOL_OPTIONS` with value `-Djdk.jar.maxSignatureFileSize=25000000`. Which variable to use depends on your project and way you run it.