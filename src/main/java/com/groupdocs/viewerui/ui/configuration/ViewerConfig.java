package com.groupdocs.viewerui.ui.configuration;

import com.groupdocs.viewer.options.HtmlViewOptions;
import com.groupdocs.viewer.options.JpgViewOptions;
import com.groupdocs.viewer.options.PdfViewOptions;
import com.groupdocs.viewer.options.PngViewOptions;
import com.groupdocs.viewerui.ui.core.ViewerType;

public class ViewerConfig {

	private final InternalCacheOptions _internalCacheOptions = InternalCacheOptions.CACHE_FOR_FIVE_MINUTES;

	private String _licensePath = null;

	private ViewerType _viewerType = ViewerType.HTML_WITH_EMBEDDED_RESOURCES;

	private HtmlViewOptions _htmlViewOptions = HtmlViewOptions.forEmbeddedResources();

	private PngViewOptions _pngViewOptions = new PngViewOptions();

	private JpgViewOptions _jpgViewOptions = new JpgViewOptions();

	private PdfViewOptions _pdfViewOptions = new PdfViewOptions();

	public String getLicensePath() {
		return _licensePath;
	}

	public ViewerConfig setLicensePath(String licensePath) {
		this._licensePath = licensePath;
		return this;
	}

	public ViewerType getViewerType() {
		return _viewerType;
	}

	public ViewerConfig setViewerType(ViewerType viewerType) {
		this._viewerType = viewerType;
		return this;
	}

	public HtmlViewOptions getHtmlViewOptions() {
		return _htmlViewOptions;
	}

	public ViewerConfig setHtmlViewOptions(HtmlViewOptions htmlViewOptions) {
		this._htmlViewOptions = htmlViewOptions;
		return this;
	}

	public PngViewOptions getPngViewOptions() {
		return _pngViewOptions;
	}

	public ViewerConfig setPngViewOptions(PngViewOptions pngViewOptions) {
		this._pngViewOptions = pngViewOptions;
		return this;
	}

	public JpgViewOptions getJpgViewOptions() {
		return _jpgViewOptions;
	}

	public ViewerConfig setJpgViewOptions(JpgViewOptions jpgViewOptions) {
		this._jpgViewOptions = jpgViewOptions;
		return this;
	}

	public PdfViewOptions getPdfViewOptions() {
		return _pdfViewOptions;
	}

	public ViewerConfig setPdfViewOptions(PdfViewOptions pdfViewOptions) {
		this._pdfViewOptions = pdfViewOptions;
		return this;
	}

	public InternalCacheOptions getInternalCacheOptions() {
		return _internalCacheOptions;
	}

	@Override
	public String toString() {
		return "ViewerConfig {" +
			   " internalCacheOptions=" + _internalCacheOptions +
			   ", licensePath='" + _licensePath + '\'' +
			   ", viewerType=" + _viewerType +
			   ", htmlViewOptions=" + _htmlViewOptions +
			   ", pngViewOptions=" + _pngViewOptions +
			   ", jpgViewOptions=" + _jpgViewOptions +
			   ", pdfViewOptions=" + _pdfViewOptions +
			   " }";
	}
}
