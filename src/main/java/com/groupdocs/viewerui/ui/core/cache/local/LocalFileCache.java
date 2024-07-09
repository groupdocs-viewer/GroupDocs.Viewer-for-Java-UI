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
 * This class provides methods to put, get, and remove cached items from the local storage based on their unique keys.
 */
public class LocalFileCache implements FileCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileCache.class);
    private Path _cachePath;
    private ISerializer _serializer;
    private long _waitTimeout = 100L;

    /**
     * Creates a new instance of {@link LocalFileCache} class.
     *
     * @param serializer  The serializer to be used for serialization/deserialization of the cache entries. Default value is {@link JacksonJsonSerializer}.
     * @param cacheConfig Configuration for the cache. It must not be null and must provide a valid cache path.
     * @throws IllegalArgumentException Thrown if {@code cacheConfig} is null or if the cache path provided in {@code cacheConfig} is null.
     * @throws IllegalStateException    Thrown if the cache path provided in {@code cacheConfig} is not accessible.
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

    /**
     * Sets the new value for the relative or absolute path to the cache folder.
     *
     * @param cachePath The new path to be set as the cache path.
     */
    public void setCachePath(Path cachePath) {
        this._cachePath = cachePath;
    }

    /**
     * Sets the wait timeout for get operations on the local file cache. This specifies how long to wait when retrieving items from the cache, in milliseconds.
     * If set to a value greater than 0, {@link LocalFileCache#get(String, String, Class)} will block until the item is available or the operation times out.
     * Default value is 100 milliseconds.
     * @param waitTimeout The new wait timeout for get operations on the local file cache in milliseconds. If set to a value less than or equal to 0, there is no waiting time.
     */
    public void setWaitTimeout(long waitTimeout) {
        this._waitTimeout = waitTimeout;
    }

    /**
     * Returns the serializer used for serialization/deserialization of cache entries.
     * @return The instance of {@link ISerializer} that is used to serialize and deserialize cache entries.
     */
    public ISerializer getSerializer() {
        if (this._serializer == null) {
            _serializer = new JacksonJsonSerializer();
        }
        return _serializer;
    }

    /**
     * Sets the serializer used for serialization/deserialization of cache entries.
     * @param serializer The instance of {@link ISerializer} that is to be set as the new serializer. If null, it will use {@link JacksonJsonSerializer}.
     */
    public void setSerializer(ISerializer serializer) {
        this._serializer = serializer;
    }

      /**
         * Retrieves and deserializes data associated with the given cache key and file path if present.
         * <p>
         * This method will block for a maximum of {@link LocalFileCache#setWaitTimeout(long)} milliseconds while waiting for an item to become available in the cache.
         * If no item is found within this time, the method returns null.
         * </p>
         * @param cacheKey  An unique identifier for the cache entry.
         * @param filePath  The relative or absolute filepath.
         * @param clazz     The class type of the object to be deserialized from the cache.
         * @return          The deserialized object or null if not found in the cache.
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
         * Serializes data from an input stream to the local disk using a specified serializer.
         * <p>
         * This method will use the {@link LocalFileCache#getSerializer()} to serialize the data before writing it to the cache file.
         * </p>
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
