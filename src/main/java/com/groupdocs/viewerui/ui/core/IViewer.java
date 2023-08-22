package com.groupdocs.viewerui.ui.core;

import com.groupdocs.viewerui.ui.core.entities.DocumentInfo;
import com.groupdocs.viewerui.ui.core.entities.FileCredentials;
import com.groupdocs.viewerui.ui.core.entities.Page;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public interface IViewer extends Closeable {

	String getPageExtension();

	Page createPage(int pageNumber, byte[] data);

	DocumentInfo getDocumentInfo(FileCredentials fileCredentials);

	Page getPage(FileCredentials fileCredentials, int pageNumber);

	List<Page> getPages(FileCredentials fileCredentials, int[] pageNumbers);

	byte[] getPdf(FileCredentials fileCredentials);

	byte[] getPageResource(FileCredentials fileCredentials, int pageNumber, String resourceName);

	void close();

}
