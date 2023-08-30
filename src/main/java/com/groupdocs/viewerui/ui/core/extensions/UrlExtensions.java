package com.groupdocs.viewerui.ui.core.extensions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlExtensions {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlExtensions.class);
    private UrlExtensions() {
    }

    public static Map<String, String> extractParams(String queryString) {
        return Arrays.stream(queryString.split("&"))
                .map(queryPart -> queryPart.split("="))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(
                        keyValue -> decode(keyValue[0]),
                        keyValue -> decode(keyValue[1])
                ));
    }

    public static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("Can't decode string using UTF-8", e);
            return URLDecoder.decode(value);
        }
    }

    public static String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("Can't encode string using UTF-8", e);
            return URLEncoder.encode(value);
        }
    }
}
