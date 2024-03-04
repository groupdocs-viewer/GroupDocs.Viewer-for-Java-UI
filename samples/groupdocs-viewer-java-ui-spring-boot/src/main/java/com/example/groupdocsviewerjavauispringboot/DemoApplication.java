package com.example.groupdocsviewerjavauispringboot;

import com.groupdocs.viewerui.handler.JakartaViewerEndpointHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class DemoApplication {
	private final JakartaViewerEndpointHandler _viewerEndpointHandler;

	public DemoApplication(JakartaViewerEndpointHandler endpointHandler) {
		this._viewerEndpointHandler = endpointHandler;
	}

	@RequestMapping("/")
	public String home(HttpServletRequest request) {
		return "<html><body><p>groupdocs-viewer-java-ui-spring-boot</p><p><a href=\"" + request.getContextPath() + "/viewer/\">Open GroupDocs.Viewer page</a></p><body/></html>";
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

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
