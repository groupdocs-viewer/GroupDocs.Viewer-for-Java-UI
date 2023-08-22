package com.groupdocs.viewerui.ui.core.extensions;

import com.groupdocs.viewer.options.HtmlViewOptions;
import com.groupdocs.viewer.options.JpgViewOptions;
import com.groupdocs.viewer.options.PdfViewOptions;
import com.groupdocs.viewer.options.PngViewOptions;

public class CopyExtensions {

	public static void copyHtmlViewOptions(HtmlViewOptions src, HtmlViewOptions dst) {
		dst.setRenderResponsive(src.isRenderResponsive());
		dst.setMinify(src.isMinify());
		dst.setRenderToSinglePage(src.isRenderToSinglePage());
		dst.setImageMaxWidth(src.getImageMaxWidth());
		dst.setImageMaxHeight(src.getImageMaxHeight());
		dst.setImageWidth(src.getImageWidth());
		dst.setImageHeight(src.getImageHeight());
		dst.setForPrinting(src.isForPrinting());
		dst.setExcludeFonts(src.isExcludeFonts());
		dst.setFontsToExclude(src.getFontsToExclude());
		dst.setFontsToExclude(src.getFontsToExclude());
		dst.setRenderComments(src.isRenderComments());
		dst.setRenderNotes(src.isRenderNotes());
		dst.setRenderHiddenPages(src.isRenderHiddenPages());
		dst.setDefaultFontName(src.getDefaultFontName());
		dst.setArchiveOptions(src.getArchiveOptions());
		dst.setCadOptions(src.getCadOptions());
		dst.setEmailOptions(src.getEmailOptions());
		dst.setOutlookOptions(src.getOutlookOptions());
		dst.setPdfOptions(src.getPdfOptions());
		dst.setProjectManagementOptions(src.getProjectManagementOptions());
		dst.setSpreadsheetOptions(src.getSpreadsheetOptions());
		dst.setWordProcessingOptions(src.getWordProcessingOptions());
	}

	public static void copyPdfViewOptions(PdfViewOptions src, PdfViewOptions dst) {
		dst.setJpgQuality(src.getJpgQuality());
		dst.setSecurity(src.getSecurity());
		dst.setImageMaxWidth(src.getImageMaxWidth());
		dst.setImageMaxHeight(src.getImageMaxHeight());
		dst.setImageWidth(src.getImageWidth());
		dst.setImageHeight(src.getImageHeight());
		dst.setRenderComments(src.isRenderComments());
		dst.setRenderNotes(src.isRenderNotes());
		dst.setRenderHiddenPages(src.isRenderHiddenPages());
		dst.setDefaultFontName(src.getDefaultFontName());
		dst.setArchiveOptions(src.getArchiveOptions());
		dst.setCadOptions(src.getCadOptions());
		dst.setEmailOptions(src.getEmailOptions());
		dst.setOutlookOptions(src.getOutlookOptions());
		dst.setPdfOptions(src.getPdfOptions());
		dst.setProjectManagementOptions(src.getProjectManagementOptions());
		dst.setSpreadsheetOptions(src.getSpreadsheetOptions());
		dst.setWordProcessingOptions(src.getWordProcessingOptions());
	}

	public static void copyPngViewOptions(PngViewOptions src, PngViewOptions dst) {
		dst.setExtractText(src.isExtractText());
		dst.setWidth(src.getWidth());
		dst.setHeight(src.getHeight());
		dst.setMaxWidth(src.getMaxWidth());
		dst.setMaxHeight(src.getMaxHeight());
		dst.setRenderComments(src.isRenderComments());
		dst.setRenderNotes(src.isRenderNotes());
		dst.setRenderHiddenPages(src.isRenderHiddenPages());
		dst.setDefaultFontName(src.getDefaultFontName());
		dst.setArchiveOptions(src.getArchiveOptions());
		dst.setCadOptions(src.getCadOptions());
		dst.setEmailOptions(src.getEmailOptions());
		dst.setOutlookOptions(src.getOutlookOptions());
		dst.setPdfOptions(src.getPdfOptions());
		dst.setProjectManagementOptions(src.getProjectManagementOptions());
		dst.setSpreadsheetOptions(src.getSpreadsheetOptions());
		dst.setWordProcessingOptions(src.getWordProcessingOptions());
	}

	public static void copyJpgViewOptions(JpgViewOptions src, JpgViewOptions dst) {
		dst.setQuality(src.getQuality());
		dst.setExtractText(src.isExtractText());
		dst.setWidth(src.getWidth());
		dst.setHeight(src.getHeight());
		dst.setMaxWidth(src.getMaxWidth());
		dst.setMaxHeight(src.getMaxHeight());
		dst.setRenderComments(src.isRenderComments());
		dst.setRenderNotes(src.isRenderNotes());
		dst.setRenderHiddenPages(src.isRenderHiddenPages());
		dst.setDefaultFontName(src.getDefaultFontName());
		dst.setArchiveOptions(src.getArchiveOptions());
		dst.setCadOptions(src.getCadOptions());
		dst.setEmailOptions(src.getEmailOptions());
		dst.setOutlookOptions(src.getOutlookOptions());
		dst.setPdfOptions(src.getPdfOptions());
		dst.setProjectManagementOptions(src.getProjectManagementOptions());
		dst.setSpreadsheetOptions(src.getSpreadsheetOptions());
		dst.setWordProcessingOptions(src.getWordProcessingOptions());
	}

}
