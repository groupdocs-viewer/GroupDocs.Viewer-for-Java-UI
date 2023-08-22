package com.groupdocs.viewerui.ui.core;

import com.groupdocs.viewerui.ui.core.entities.FileSystemEntry;

import java.util.List;

public interface IFileStorage {

	List<FileSystemEntry> listDirsAndFiles(String folderPath);

	byte[] readFile(String filePath);

	String writeFile(String fileName, byte[] bytes, boolean rewrite);

}
