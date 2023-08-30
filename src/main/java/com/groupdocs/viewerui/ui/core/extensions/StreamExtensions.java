package com.groupdocs.viewerui.ui.core.extensions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamExtensions {

    private static final int BUFFER_SIZE = 8192;

    private StreamExtensions() {
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            int bytesRead;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            return output.toByteArray();
        }
    }
}
