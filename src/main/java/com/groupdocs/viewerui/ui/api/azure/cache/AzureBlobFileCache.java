package com.groupdocs.viewerui.ui.api.azure.cache;

import com.azure.core.util.BinaryData;
import com.azure.core.util.CoreUtils;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobProperties;
import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.api.azure.AzureBlobOptions;
import com.groupdocs.viewerui.ui.api.cache.FileCache;
import com.groupdocs.viewerui.ui.core.extensions.FilesExtensions;
import com.groupdocs.viewerui.ui.core.serialize.ISerializer;
import com.groupdocs.viewerui.ui.core.serialize.JacksonJsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * A file cache implementation using Azure Blob Storage for storage backend.
 */
public class AzureBlobFileCache implements FileCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureBlobFileCache.class);

    private final BlobContainerClient _blobContainerClient;
    private ISerializer _serializer;
    private boolean _replaceWhenExists;
    private long _cacheEvictionThresholdTime = 30 * 24 * 60 * 1000; // Default to 30 days in milliseconds

    /**
     * Constructs an AzureBlobFileCache instance with provided Azure Blob options.
     * If no serializer is provided, a default JSON serializer (JacksonJsonSerializer) will be used.
     *
     * @param azureBlobOptions The configuration options for connecting to the Azure Blob Storage service.
     */
    public AzureBlobFileCache(AzureBlobOptions azureBlobOptions) {
        this(azureBlobOptions, null, false);
    }

    /**
     * Constructs an AzureBlobFileCache instance with provided Azure Blob options and a flag to determine whether to replace existing files.
     * If no serializer is provided, a default JSON serializer (JacksonJsonSerializer) will be used.
     *
     * @param azureBlobOptions  The configuration options for connecting to the Azure Blob Storage service.
     * @param replaceWhenExists Flag indicating if existing files should be replaced when setting new data.
     */
    public AzureBlobFileCache(AzureBlobOptions azureBlobOptions, boolean replaceWhenExists) {
        this(azureBlobOptions, null, replaceWhenExists);
    }

    /**
     * Constructs an AzureBlobFileCache instance with provided Azure Blob options and a serializer.
     *
     * @param azureBlobOptions The configuration options for connecting to the Azure Blob Storage service.
     * @param serializer       An optional serializer for serializing objects stored in the cache. If not provided, a default JSON serializer will be used.
     */
    public AzureBlobFileCache(AzureBlobOptions azureBlobOptions, ISerializer serializer) {
        this(azureBlobOptions, serializer, false);
    }

    /**
     * Constructs an AzureBlobFileCache instance with provided Azure Blob options, a serializer, and a flag to determine whether to replace existing files.
     *
     * @param azureBlobOptions  The configuration options for connecting to the Azure Blob Storage service.
     * @param serializer        An optional serializer for serializing objects stored in the cache. If not provided, a default JSON serializer will be used.
     * @param replaceWhenExists Flag indicating if existing files should be replaced when setting new data.
     */
    public AzureBlobFileCache(AzureBlobOptions azureBlobOptions, ISerializer serializer, boolean replaceWhenExists) {
        if (azureBlobOptions == null) {
            throw new IllegalArgumentException("azureBlobOptions");
        }

        // Initialize the BlobContainerClient using the provided options
        final BlobContainerClientBuilder builder = new BlobContainerClientBuilder()
                .containerName(azureBlobOptions.getContainerName())
                .connectionString(
                        "DefaultEndpointsProtocol=" + azureBlobOptions.getDefaultEndpointsProtocol()
                                + ";AccountName=" + azureBlobOptions.getAccountName()
                                + ";AccountKey=" + azureBlobOptions.getAccountKey()
                                + ";EndpointSuffix=" + azureBlobOptions.getEndpointSuffix())
                .clientOptions(azureBlobOptions.getClientOptions());

        this._blobContainerClient = builder.buildClient();
        this._serializer = serializer;
        this._replaceWhenExists = replaceWhenExists;
    }

    /**
     * Retrieves an object from the cache, deserializing it if necessary.
     * The method checks if the cache entry has expired before retrieving it.
     *
     * @param <T>      The type of the object to retrieve.
     * @param cacheKey The key identifying the cached data.
     * @param filePath The file path where the data is stored in the cache.
     * @param clazz    The class type to which the retrieved data should be deserialized.
     * @return The deserialized object, or null if no such entry exists or it has expired.
     */
    @Override
    public <T> T get(String cacheKey, String filePath, Class<T> clazz) {
        String cacheFilePath = getCacheFilePath(cacheKey, filePath);
        try {
            if (isAzureBlobNotExists(cacheFilePath)) {
                return null;
            } else {
                final BlobClient blobClient = getBlobContainerClient().getBlobClient(cacheFilePath);
                final BlobProperties properties = blobClient.getProperties();

                if (properties != null) {
                    final OffsetDateTime lastModified = properties.getLastModified();
                    if (lastModified != null) {
                        final long expirationTime = lastModified.toLocalDateTime().toEpochSecond(ZoneOffset.UTC) + getCacheEvictionThresholdTime();
                        if (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) > expirationTime) {
                            LOGGER.debug("Cache entry expired: cacheKey=" + cacheKey + " filePath=" + filePath);
                            // Delete Azure blob object
                            blobClient.delete();
                            return null;
                        }
                    }
                }
                final BinaryData binaryData = blobClient.downloadContent();

                if (InputStream.class.equals(clazz)) {
                    return (T) binaryData.toStream();
                } else if (byte[].class.equals(clazz)) {
                    return (T) binaryData.toBytes();
                } else {
                    try (InputStream inputStream = binaryData.toStream()) {
                        final ISerializer serializer = getSerializer();
                        return serializer.deserialize(inputStream, clazz);
                    } catch (Exception e) {
                        LOGGER.error("Can't deserialize local file cache entry: cacheKey=" + cacheKey + ", clazz=" + clazz, e);
                        throw e;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Exception throws while getting cache entry: filePath={}", filePath, e);
            throw new ViewerUiException(e);
        }
    }

    /**
     * Checks is file with specified key exists in Azure Blob Storage
     */
    protected boolean isAzureBlobNotExists(String key) {
        final BlobContainerClient blobContainerClient = getBlobContainerClient();

        return !blobContainerClient.getBlobClient(key).exists();
    }

    private String getCacheFilePath(String cacheKey, String filePath) {
        if (CoreUtils.isNullOrEmpty(cacheKey)) {
            throw new IllegalArgumentException("cacheKey");
        }
        if (CoreUtils.isNullOrEmpty(filePath)) {
            throw new IllegalArgumentException("filePath");
        }

        String[] parts = filePath.split("[" + String.join("", FilesExtensions.INVALID_PATH_CHARS) + "]");
        String cacheSubFolder = String.join("_", parts).replace(".", "_");

        final String cacheFilePath = cacheSubFolder + File.separator + cacheKey;

        LOGGER.trace("cacheKey={}, filePath={}, cacheFilePath={}", cacheKey, filePath, cacheFilePath);

        return cacheFilePath;
    }

    /**
     * Writes data to the Azure Blob Storage using a byte array and sets it as an entry in the file cache.
     * If {@code replaceWhenExists} is true, the existing entry with the same key will be overwritten. Otherwise, if the entry already exists, no action will be taken.
     * The method checks for expiration based on the last modified date of the Azure Blob Storage object and deletes it if expired.
     *
     * @param cacheKey The unique identifier used to identify the cached data.
     * @param filePath The file path where the data is stored in the cache.
     * @param bytes    The byte array containing the data to be written to Azure Blob Storage.
     */
    @Override
    public void set(String cacheKey, String filePath, byte[] bytes) {
        if (bytes == null) {
            return;
        }

        String cacheFilePath = getCacheFilePath(cacheKey, filePath);
        try {
            final BlobClient blobClient = getBlobContainerClient().getBlobClient(cacheFilePath);
            blobClient.upload(BinaryData.fromBytes(bytes), isReplaceWhenExists());
        } catch (Exception e) {
            LOGGER.error("Exception throws while saving byte array to Azure Blob Storage cache: cacheKey=" + cacheKey + ", filePath=" + filePath, e);
            throw new ViewerUiException(e);
        }
    }

    /**
     * Writes data from an InputStream to the Azure Blob Storage.
     * If {@code replaceWhenExists} is true, the existing entry with the same key will be overwritten. Otherwise, if the entry already exists, no action will be taken.
     * The method checks for expiration based on the last modified date of the Azure Blob Storage object and deletes it if expired.
     *
     * @param cacheKey    The unique identifier used to identify the cached data.
     * @param filePath    The file path where the data is stored in the cache.
     * @param inputStream The InputStream from which the data will be read.
     */
    @Override
    public void set(String cacheKey, String filePath, InputStream inputStream) {
        if (inputStream == null) {
            return;
        }

        String cacheFilePath = getCacheFilePath(cacheKey, filePath);
        try {
            final BlobClient blobClient = getBlobContainerClient().getBlobClient(cacheFilePath);
            blobClient.upload(BinaryData.fromStream(inputStream), isReplaceWhenExists());
        } catch (Exception e) {
            LOGGER.error("Exception throws while saving byte array to Azure Blob Storage cache: cacheKey=" + cacheKey + ", filePath=" + filePath, e);
            throw new ViewerUiException(e);
        }
    }

    /**
     * Saves the given object to Azure Blob Storage under the specified cache key and file path.
     *
     * <p>This method checks if the object is null and returns immediately if it is.
     *
     * @param cacheKey The key that identifies the cache entry.
     * @param filePath The path within the cache where the object should be stored.
     * @param object   The object to be cached. If null, the method returns immediately without any action.
     * @throws ViewerUiException if an exception occurs while saving the byte array to Azure Blob Storage.
     */
    @Override
    public void set(String cacheKey, String filePath, Object object) {
        if (object == null) {
            return;
        }

        String cacheFilePath = getCacheFilePath(cacheKey, filePath);
        try {
            final BlobClient blobClient = getBlobContainerClient().getBlobClient(cacheFilePath);
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                final ISerializer serializer = getSerializer();
                serializer.serialize(object, outputStream);
                try (InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
                    blobClient.upload(BinaryData.fromStream(inputStream), isReplaceWhenExists());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception throws while saving byte array to Azure Blob Storage cache: cacheKey=" + cacheKey + ", filePath=" + filePath, e);
            throw new ViewerUiException(e);
        }
    }

    private BlobContainerClient getBlobContainerClient() {
        return _blobContainerClient;
    }

    /**
     * Get the serializer used for serializing and deserializing objects stored in the cache.
     * If no serializer has been explicitly set, a default JSON serializer (JacksonJsonSerializer) will be created and returned.
     *
     * @return The serializer instance.
     */
    public ISerializer getSerializer() {
        if (this._serializer == null) {
            _serializer = new JacksonJsonSerializer();
        }
        return _serializer;
    }

    /**
     * Set the serializer used for serializing and deserializing objects stored in the cache.
     *
     * @param serializer The serializer instance to be used.
     */
    public void setSerializer(ISerializer serializer) {
        this._serializer = serializer;
    }

    /**
     * Check if existing files should be replaced when setting new data.
     *
     * @return True if existing files should be replaced, false otherwise.
     */
    public boolean isReplaceWhenExists() {
        return _replaceWhenExists;
    }

    /**
     * Set the flag indicating whether existing files should be replaced when setting new data.
     *
     * @param replaceWhenExists Flag indicating if existing files should be replaced.
     */
    public void setReplaceWhenExists(boolean replaceWhenExists) {
        this._replaceWhenExists = replaceWhenExists;
    }

    /**
     * Get the threshold time for evicting cached entries.
     * Entries older than this value will be considered expired and removed from the cache during get operations.
     * The default value is 30 days (30 * 24 * 60 * 1000 milliseconds).
     *
     * @return The threshold time in milliseconds for evicting cached entries.
     */
    public long getCacheEvictionThresholdTime() {
        return _cacheEvictionThresholdTime;
    }

    /**
     * Set the threshold time for evicting cached entries. Entries older than this value will be considered expired and removed from the cache during get operations.
     * The default value is 30 days (30 * 24 * 60 * 1000 milliseconds).
     *
     * @param cacheEvictionThresholdTime The threshold time in milliseconds for evicting cached entries.
     */
    public void setCacheEvictionThresholdTime(long cacheEvictionThresholdTime) {
        this._cacheEvictionThresholdTime = cacheEvictionThresholdTime;
    }
}
