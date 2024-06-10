package com.groupdocs.viewerui.ui.core.cache.local;

import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.api.cache.FileCache;
import com.groupdocs.viewerui.ui.core.cache.local.config.LocalCacheConfig;
import com.groupdocs.viewerui.ui.core.extensions.FilesExtensions;
import com.groupdocs.viewerui.ui.core.extensions.StreamExtensions;
import com.groupdocs.viewerui.ui.core.serialize.ISerializer;
import com.groupdocs.viewerui.ui.core.serialize.JacksonJsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Represents a local file cache that allows storing and retrieving data using keys. The data is stored on the local disk.
 */
public class LocalFileCache implements FileCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileCache.class);
    private Path _cachePath;
    private ISerializer _serializer;
    private long _waitTimeout = 100L;

    /**
     * Creates new instance of {@link LocalFileCache} class.
     *
     * @param serializer  Serializer to be used for serialization/deserialization of the cache entries. Default value is {@link JacksonJsonSerializer}.
     * @param cacheConfig Configuration for the cache.
     * @throws IllegalArgumentException Thrown when {@code cacheConfig} is null.
     * @throws IllegalStateException    Thrown when {@link  LocalCacheConfig#getCachePath()} is null.
     */
    public LocalFileCache(ISerializer serializer, LocalCacheConfig cacheConfig) {
        if (cacheConfig == null) {
            throw new IllegalArgumentException("cacheConfig");
        }
        final Path cachePath = cacheConfig.getCachePath();
        if (cachePath == null) {
            throw new IllegalStateException("cachePath is null");
        }

        _serializer = serializer;
        _cachePath = cachePath;
    }

    /**
     * Returns the relative or absolute path to the cache folder.
     *
     * @return the path to the cache folder
     */
    public Path getCachePath() {
        return _cachePath;
    }

    public void setCachePath(Path cachePath) {
        this._cachePath = cachePath;
    }

    public long getWaitTimeout() {
        return _waitTimeout;
    }

    public void setWaitTimeout(long waitTimeout) {
        this._waitTimeout = waitTimeout;
    }

    public ISerializer getSerializer() {
        if (this._serializer == null) {
            _serializer = new JacksonJsonSerializer();
        }
        return _serializer;
    }

    public void setSerializer(ISerializer serializer) {
        this._serializer = serializer;
    }

    /**
     * Retrieves and deserializes data associated with the given cache key and file path if present.
     *
     * @param cacheKey An unique identifier for the cache entry.
     * @param filePath The relative or absolute filepath.
     * @return the object or null if not found in the cache.
     */
    public <T> T get(String cacheKey, String filePath, Class<T> clazz) {
        Path cacheFilePath = getCacheFilePath(cacheKey, filePath);

        if (Files.exists(cacheFilePath)) {
            return deserialize(cacheFilePath, clazz);
        }

        return null;
    }

    /**
     * Saves a byte array to the local disk using the specified cache key and file path.
     *
     * @param cacheKey An unique identifier for the cache entry.
     * @param filePath The relative or absolute filepath.
     * @param value    The byte array to save. If null, the method returns without doing anything.
     */
    public void set(String cacheKey, String filePath, byte[] value) {
        if (value == null) {
            return;
        }

        Path cacheFilePath = getCacheFilePath(cacheKey, filePath);
        try (final OutputStream outputStream = createOutputStream(cacheFilePath)) {
            outputStream.write(value);
        } catch (IOException e) {
            LOGGER.error("Exception throws while saving byte array to local file cache: cacheKey={}, filePath={}", cacheKey, filePath, e);
            throw new ViewerUiException(e);
        }
    }

    /**
     * Serializes data from an input stream to the local disk.
     *
     * @param cacheKey    An unique identifier for the cache entry.
     * @param filePath    The relative or absolute filepath.
     * @param inputStream The stream to serialize and save to the cache. If null, the method returns without doing anything.
     */
    public void set(String cacheKey, String filePath, InputStream inputStream) {
        if (inputStream == null) {
            return;
        }

        Path cacheFilePath = getCacheFilePath(cacheKey, filePath);
        try (final OutputStream outputStream = createOutputStream(cacheFilePath)) {
            outputStream.write(StreamExtensions.toByteArray(inputStream));
        } catch (IOException e) {
            LOGGER.error("Exception throws while saving stream to local file cache: cacheKey={}, filePath={}", cacheKey, filePath, e);
            throw new ViewerUiException(e);
        }
    }

    /**
     * Serializes an object to the local disk.
     *
     * @param cacheKey An unique identifier for the cache entry.
     * @param filePath The relative or absolute filepath.
     * @param value    The object to serialize and save to the cache. If null, the method returns without doing anything.
     */
    public void set(String cacheKey, String filePath, Object value) {
        if (value == null) {
            return;
        }

        Path cacheFilePath = getCacheFilePath(cacheKey, filePath);
        try (final OutputStream outputStream = createOutputStream(cacheFilePath)) {
            final ISerializer serializer = getSerializer();
            serializer.serialize(value, outputStream);
        } catch (IOException e) {
            LOGGER.error("Exception throws while saving object to local file cache: cacheKey={}, filePath={}", cacheKey, filePath, e);
            throw new ViewerUiException(e);
        }
    }

    private <T> T deserialize(Path cachePath, Class<T> clazz) {
        T data = null;
        try {
            if (clazz.isAssignableFrom(InputStream.class)) {
                data = (T) createInputStream(_cachePath);
            } else if (clazz.isAssignableFrom(byte[].class)) {
                data = (T) getBytes(cachePath);
            } else {
                try (final InputStream inputStream = createInputStream(cachePath)) {
                    final ISerializer ISerializer = getSerializer();
                    data = ISerializer.deserialize(inputStream, clazz);
                }
            }
        } catch (Exception e) {

            LOGGER.debug("Can't deserialize local file cache entry: cachePath={}, clazz={}", cachePath, clazz, e);
        }

        return data;
    }

    private Path getCacheFilePath(String cacheKey, String filePath) {
        String[] parts = filePath.split("[" + String.join("", FilesExtensions.INVALID_PATH_CHARS) + "]");
        String cacheSubFolder = String.join("_", parts).replace(".", "_");

        final Path cachePath = getCachePath();
        final Path cacheDirPath = cachePath.resolve(cacheSubFolder);
        final Path cacheFilePath = cacheDirPath.resolve(cacheKey);

        if (Files.notExists(cacheFilePath)) {
            try {
                Files.createDirectories(cacheDirPath);
            } catch (IOException e) {
                LOGGER.error("Exception throws while creating cache file directory: cacheFilePath={}", cacheFilePath, e);
                throw new ViewerUiException(e);
            }
        }

        return cacheFilePath;
    }

    private InputStream createInputStream(Path path) {
        InputStream stream = null;
        long interval = 50L;
        long totalTime = 0L;

        while (stream == null) {
            try {
                stream = Files.newInputStream(path);
            } catch (IOException e) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException ex) {
                    LOGGER.error("Exception throws while waiting for next try to create input stream to cache file: path={}", path, e);
                    throw new ViewerUiException(e);
                }
                totalTime += interval;

                if (_waitTimeout != 0L && totalTime > _waitTimeout) {
                    throw new ViewerUiException(e);
                }
            }
        }

        return stream;
    }

    private OutputStream createOutputStream(Path path) {
        OutputStream stream = null;
        long interval = 50L;
        long totalTime = 0L;

        while (stream == null) {
            try {
                stream = Files.newOutputStream(path);
            } catch (IOException e) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException ex) {
                    LOGGER.error("Exception throws while waiting for next try to create output stream to cache file: path={}", path, e);
                    throw new ViewerUiException(e);
                }
                totalTime += interval;

                if (_waitTimeout != 0L && totalTime > _waitTimeout) {
                    throw new ViewerUiException(e);
                }
            }
        }

        return stream;
    }

    private byte[] getBytes(Path path) {
        byte[] bytes = null;
        long interval = 50L;
        long totalTime = 0L;

        while (bytes == null) {
            try {
                bytes = Files.readAllBytes(path);
            } catch (IOException e) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException ex) {
                    LOGGER.error("Exception throws while waiting for next try to read cache file to byte array: path={}", path, e);
                    throw new ViewerUiException(e);
                }
                totalTime += interval;

                if (_waitTimeout != 0 && totalTime > _waitTimeout) {
                    throw new ViewerUiException(e);
                }
            }
        }

        return bytes;
    }
}
