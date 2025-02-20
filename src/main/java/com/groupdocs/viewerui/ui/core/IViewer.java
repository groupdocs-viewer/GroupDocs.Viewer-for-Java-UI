package com.groupdocs.viewerui.ui.core;

import com.groupdocs.viewerui.ui.core.entities.*;

import java.io.Closeable;
import java.util.List;

public interface IViewer extends Closeable {

	String getPageExtension();

	String getThumbExtension();

	Page createPage(int pageNumber, byte[] data);

	Thumb createThumb(int pageNumber, byte[] data);

	DocumentInfo getDocumentInfo(FileCredentials fileCredentials);

	Page getPage(FileCredentials fileCredentials, int pageNumber);

	Thumb getThumb(FileCredentials fileCredentials, int pageNumber);

	List<Page> getPages(FileCredentials fileCredentials, int[] pageNumbers);

	byte[] getPdf(FileCredentials fileCredentials);

	byte[] getPageResource(FileCredentials fileCredentials, int pageNumber, String resourceName);

	void close();

    List<Thumb> getThumbs(FileCredentials fileCredentials, int[] pagesToCreate);
}
