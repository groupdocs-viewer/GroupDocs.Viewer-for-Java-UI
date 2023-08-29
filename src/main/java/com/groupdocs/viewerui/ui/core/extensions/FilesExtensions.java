package com.groupdocs.viewerui.ui.core.extensions;

import com.groupdocs.viewerui.exception.ViewerUiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

public class FilesExtensions {
    public static final String[] INVALID_PATH_CHARS = new String[]{"\\", "/", ":", "*", "?", "\"", "<", ">", "|"};
    private static final Logger LOGGER = LoggerFactory.getLogger(FilesExtensions.class);
    public static Stream<Path> list(Path dir) {
        try {
            return Files.list(dir);
        } catch (IOException e) {
            LOGGER.error("Exception throws while listing directory content: dir={}", dir, e);
            throw new ViewerUiException(e);
        }
    }

    public static boolean isHidden(Path path) {
        try {
            return Files.isHidden(path);
        } catch (IOException e) {
            LOGGER.error("Exception throws while Checking is file hidden: path={}", path, e);
            throw new ViewerUiException(e);
        }
    }

    public static <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) {
        try {
            return Files.readAttributes(path, type, options);
        } catch (IOException e) {
            LOGGER.error("Exception throws while reading file attribute: path={}", path, e);
            throw new ViewerUiException(e);
        }
    }

    public static long size(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            LOGGER.error("Exception throws while getting file size: path={}", path, e);
            throw new ViewerUiException(e);
        }
    }
}
