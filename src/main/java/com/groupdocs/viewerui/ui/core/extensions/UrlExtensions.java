package com.groupdocs.viewerui.ui.core.extensions;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlExtensions {
    private UrlExtensions() {
    }

    public static Map<String, String> extractParams(String queryString) {
        return Arrays.stream(queryString.split("&"))
                .map(queryPart -> queryPart.split("="))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(
                        keyValue -> URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                        keyValue -> URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                ));
    }
}
