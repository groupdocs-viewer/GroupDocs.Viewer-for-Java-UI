package com.groupdocs.viewerui.ui.core.extensions;

import com.groupdocs.viewer.options.*;

public class CopyExtensions {

    /**
     * Copies the properties of the source HtmlViewOptions to the destination HtmlViewOptions.
     *
     * @param dst The destination HtmlViewOptions.
     * @param src The source HtmlViewOptions.
     */
    public static void copyViewOptions(HtmlViewOptions src, HtmlViewOptions dst) {
        copyBaseViewOptions(dst, src);
        copyHtmlViewOptions(dst, src);
    }

    /**
     * Copies the properties of the source PdfViewOptions to the destination PdfViewOptions.
     *
     * @param dst The destination PdfViewOptions.
     * @param src The source PdfViewOptions.
     */
    public static void copyViewOptions(PdfViewOptions src, PdfViewOptions dst) {
        copyBaseViewOptions(dst, src);
        copyPdfViewOptions(dst, src);
    }

    /**
     * Copies the properties of the source PngViewOptions to the destination PngViewOptions.
     *
     * @param dst The destination PngViewOptions.
     * @param src The source PngViewOptions.
     */
    public static void copyViewOptions(PngViewOptions src, PngViewOptions dst) {
        copyBaseViewOptions(dst, src);
        copyPngViewOptions(dst, src);
    }

    /**
     * Copies the properties of the source JpgViewOptions to the destination JpgViewOptions.
     *
     * @param dst The destination JpgViewOptions.
     * @param src The source JpgViewOptions.
     */
    public static void copyViewOptions(JpgViewOptions src, JpgViewOptions dst) {
        copyBaseViewOptions(dst, src);
        copyJpgViewOptions(dst, src);
    }

    /**
     * Copies the properties of the source BaseViewOptions to the destination BaseViewOptions.
     *
     * @param dst The destination BaseViewOptions.
     * @param src The source BaseViewOptions.
     */
    public static void copyBaseViewOptions(BaseViewOptions src, BaseViewOptions dst) {
        //dst.setRemoveComments(src.isRemoveComments());
        dst.setRenderNotes(src.isRenderNotes());
        dst.setRenderHiddenPages(src.isRenderHiddenPages());
        dst.setDefaultFontName(src.getDefaultFontName());
        dst.setArchiveOptions(src.getArchiveOptions());
        dst.setCadOptions(src.getCadOptions());
        dst.setEmailOptions(src.getEmailOptions());
        dst.setOutlookOptions(src.getOutlookOptions());
        dst.setMailStorageOptions(src.getMailStorageOptions());
        dst.setPdfOptions(src.getPdfOptions());
        dst.setProjectManagementOptions(src.getProjectManagementOptions());
        dst.setSpreadsheetOptions(src.getSpreadsheetOptions());
        dst.setWordProcessingOptions(src.getWordProcessingOptions());
        dst.setVisioRenderingOptions(src.getVisioRenderingOptions());
        dst.setTextOptions(src.getTextOptions());
        dst.setPresentationOptions(src.getPresentationOptions());
        dst.setWebDocumentOptions(src.getWebDocumentOptions());
    }

    /**
     * Copies the properties of the source PdfViewOptions to the destination PdfViewOptions.
     *
     * @param dst The destination PdfViewOptions.
     * @param src The source PdfViewOptions.
     */
	private static void copyPdfViewOptions(PdfViewOptions src, PdfViewOptions dst) {
        dst.setSecurity(src.getSecurity());
        dst.setImageMaxWidth(src.getImageMaxWidth());
        dst.setImageMaxHeight(src.getImageMaxHeight());
        dst.setImageWidth(src.getImageWidth());
        dst.setImageHeight(src.getImageHeight());
    }

    /**
     * Copies the properties of the source PngViewOptions to the destination PngViewOptions.
     *
     * @param dst The destination PngViewOptions.
     * @param src The source PngViewOptions.
     */
	private static void copyPngViewOptions(PngViewOptions src, PngViewOptions dst) {
        dst.setExtractText(src.isExtractText());
        dst.setWidth(src.getWidth());
        dst.setHeight(src.getHeight());
        dst.setMaxWidth(src.getMaxWidth());
        dst.setMaxHeight(src.getMaxHeight());
    }

    /**
     * Copies the properties of the source JpgViewOptions to the destination JpgViewOptions.
     *
     * @param dst The destination JpgViewOptions.
     * @param src The source JpgViewOptions.
     */
    private static void copyJpgViewOptions(JpgViewOptions src, JpgViewOptions dst) {
        dst.setQuality(src.getQuality());
        dst.setExtractText(src.isExtractText());
        dst.setWidth(src.getWidth());
        dst.setHeight(src.getHeight());
        dst.setMaxWidth(src.getMaxWidth());
        dst.setMaxHeight(src.getMaxHeight());
    }

    /**
     * Copies the properties of the source HtmlViewOptions to the destination HtmlViewOptions.
     *
     * @param dst The destination HtmlViewOptions.
     * @param src The source HtmlViewOptions.
     */
	private static void copyHtmlViewOptions(HtmlViewOptions src, HtmlViewOptions dst) {
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

}
