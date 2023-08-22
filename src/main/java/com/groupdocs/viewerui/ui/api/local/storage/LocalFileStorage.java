package com.groupdocs.viewerui.ui.api.local.storage;

import com.groupdocs.viewerui.ui.core.IFileStorage;
import com.groupdocs.viewerui.ui.core.entities.FileSystemEntry;
import com.groupdocs.viewerui.ui.core.extensions.FilesExtensions;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalFileStorage implements IFileStorage {
	private final Path _storagePath;
	private long _waitTimeout = 100L;

	public LocalFileStorage(Path storagePath)
	{
		_storagePath = storagePath;
	}

	@Override
	public List<FileSystemEntry> listDirsAndFiles(String folderPath) {
		Path folderFullPath = folderPath == null || folderPath.isEmpty()
				? _storagePath
				: _storagePath.resolve(folderPath);

		Stream<FileSystemEntry> dirs = FilesExtensions.list(folderFullPath)
				.filter(Files::isDirectory)
                .filter(fileInfo -> !FilesExtensions.isHidden(fileInfo))
                .sorted(Comparator.comparing(file -> file.getFileName().toString()))
                .sorted((file1, file2) -> {
					BasicFileAttributes attrs1 = FilesExtensions.readAttributes(file1, BasicFileAttributes.class);
					BasicFileAttributes attrs2 = FilesExtensions.readAttributes(file2, BasicFileAttributes.class);
					return Long.compare(attrs1.creationTime().toMillis(), attrs2.creationTime().toMillis());
				})
                .map(directory ->
				 FileSystemEntry.directory(directory.getFileName().toString(), _storagePath.relativize(directory.toAbsolutePath().normalize()).toString(), 0L));

		Stream<FileSystemEntry> files = FilesExtensions.list(folderFullPath)
							.filter(Files::isRegularFile)
							.filter(fileInfo -> !FilesExtensions.isHidden(fileInfo))
							.sorted(Comparator.comparing(file -> file.getFileName().toString()))
							.sorted((file1, file2) -> {
								BasicFileAttributes attrs1 = FilesExtensions.readAttributes(file1, BasicFileAttributes.class);
								BasicFileAttributes attrs2 = FilesExtensions.readAttributes(file2, BasicFileAttributes.class);
								return Long.compare(attrs1.creationTime().toMillis(), attrs2.creationTime().toMillis());
							})
                .map(file ->
				 FileSystemEntry.file(file.getFileName().toString(), _storagePath.relativize(file.toAbsolutePath().normalize()).toString(), FilesExtensions.size(file)));

		Stream<FileSystemEntry> dirsAndFiles = Stream.concat(dirs, files);
		return dirsAndFiles.collect(Collectors.toList());
	}

	@Override
	public byte[] readFile(String filePath) {
		Path fullPath = _storagePath.resolve(filePath);
		try (InputStream fs = createStream(fullPath)) {
			return IOUtils.toByteArray(fs);
		} catch (Exception e) {
			e.printStackTrace(); // TODO: Add logging
			throw new RuntimeException(e);
        }
    }

	@Override
	public String writeFile(String fileName, byte[] bytes, boolean rewrite) {
		throw new IllegalStateException("Not implemented");
	}


	private InputStream createStream(Path path) throws InterruptedException, FileNotFoundException {

		InputStream stream = null;
		int interval = 50;
		long totalTime = 0L;

		while (stream == null) {
			try {
				stream = new FileInputStream(path.toFile());
			} catch (IOException e) {
				Thread.sleep(interval);
				totalTime += interval;

				if (_waitTimeout != 0L && totalTime > _waitTimeout) {
					throw e;
				}
			}
		}
		return stream;
	}

	public long getWaitTimeout() {
		return _waitTimeout;
	}

	public void setWaitTimeout(long waitTimeout) {
		this._waitTimeout = waitTimeout;
	}
}
