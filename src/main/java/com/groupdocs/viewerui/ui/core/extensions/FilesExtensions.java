package com.groupdocs.viewerui.ui.core.extensions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

public class FilesExtensions {
    public static Stream<Path> list(Path dir) {
        try {
            return Files.list(dir);
        } catch (IOException e) {
            e.printStackTrace(); // TODO: Add logging
            throw new RuntimeException(e);
        }
    }

    public static boolean isHidden(Path path) {
        try {
            return Files.isHidden(path);
        } catch (IOException e) {
            e.printStackTrace(); // TODO: Add logging
            throw new RuntimeException(e);
        }
    }

    public static <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) {
        try {
            return Files.readAttributes(path, type, options);
        } catch (IOException e) {
            e.printStackTrace(); // TODO: Add logging
            throw new RuntimeException(e);
        }
    }

    public static long size(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            e.printStackTrace(); // TODO: Add logging
            throw new RuntimeException(e);
        }
    }
}
