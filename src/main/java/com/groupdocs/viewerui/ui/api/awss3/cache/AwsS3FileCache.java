package com.groupdocs.viewerui.ui.api.awss3.cache;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.StringUtils;
import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.api.awss3.AwsS3Options;
import com.groupdocs.viewerui.ui.api.cache.FileCache;
import com.groupdocs.viewerui.ui.core.extensions.FilesExtensions;
import com.groupdocs.viewerui.ui.core.serialize.ISerializer;
import com.groupdocs.viewerui.ui.core.serialize.JacksonJsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;

/**
 * Represents a cache for files stored in an AWS S3 bucket.
 * This class implements the FileCache interface, which provides methods for getting and setting cached file data.
 */
public class AwsS3FileCache implements FileCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsS3FileCache.class);

    private final String _bucketName;
    private final AmazonS3 _s3Client;
    private ISerializer _serializer;
    private boolean _replaceWhenExists;
    private long _cacheEvictionThresholdTime = 30 * 24 * 60 * 1000;

    /**
     * Constructs an AwsS3FileCache instance with provided AWS S3 options.
     *
     * @param awsS3Options The configuration options for connecting to the AWS S3 service.
     */
    public AwsS3FileCache(AwsS3Options awsS3Options) {
        this(awsS3Options, null, false);
    }

    /**
     * Constructs an AwsS3FileCache instance with provided AWS S3 options and a flag to determine whether to replace existing files.
     *
     * @param awsS3Options      The configuration options for connecting to the AWS S3 service.
     * @param replaceWhenExists Flag indicating if existing files should be replaced when setting new data.
     */
    public AwsS3FileCache(AwsS3Options awsS3Options, boolean replaceWhenExists) {
        this(awsS3Options, null, replaceWhenExists);
    }

    /**
     * Constructs an AwsS3FileCache instance with provided AWS S3 options and a serializer.
     *
     * @param awsS3Options      The configuration options for connecting to the AWS S3 service.
     * @param serializer        An optional serializer for serializing objects stored in the cache. If not provided, a default JSON serializer will be used.
     */
    public AwsS3FileCache(AwsS3Options awsS3Options, ISerializer serializer) {
        this(awsS3Options, serializer, false);
    }

    /**
     * Constructs an AwsS3FileCache instance with provided AWS S3 options, a serializer, and a flag to determine whether to replace existing files.
     *
     * @param awsS3Options      The configuration options for connecting to the AWS S3 service.
     * @param serializer        An optional serializer for serializing objects stored in the cache. If not provided, a default JSON serializer will be used.
     * @param replaceWhenExists Flag indicating if existing files should be replaced when setting new data.
     */
    public AwsS3FileCache(AwsS3Options awsS3Options, ISerializer serializer, boolean replaceWhenExists) {
        if (awsS3Options == null) {
            throw new IllegalArgumentException("awsS3Options");
        }

        if (StringUtils.isNullOrEmpty(awsS3Options.getRegion())) {
            throw new IllegalArgumentException("awsS3Options#getRegion() is null or empty");
        }
        this._bucketName = awsS3Options.getBucketName();

        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
        if (awsS3Options.getRegion() != null) {
            builder.withRegion(awsS3Options.getRegion());
        }
        boolean keysProvided = !StringUtils.isNullOrEmpty(awsS3Options.getAccessKey()) &&
                !StringUtils.isNullOrEmpty(awsS3Options.getSecretKey());
        if (keysProvided) {
            builder.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsS3Options.getAccessKey(), awsS3Options.getSecretKey())));
        } else {
            builder.withCredentials(new EnvironmentVariableCredentialsProvider());
        }
        if (awsS3Options.getClientConfiguration() != null) {
            builder.withClientConfiguration(awsS3Options.getClientConfiguration());
        }
        this._s3Client = builder.build();
        this._serializer = serializer;
        this._replaceWhenExists = replaceWhenExists;
    }

    /**
     * Retrieves a cached file entry based on the cache key and file path. Deserializes the data into an instance of the specified class.
     * If the cache entry does not exist, returns null.
     *
     * @param <T>      The type to deserialize the cached data into.
     * @param cacheKey The cache key associated with the file entry.
     * @param filePath The path of the file in the cache.
     * @param clazz    The class object representing the desired type for deserialization.
     * @return An instance of the specified type, or null if the cache entry does not exist.
     */
    @Override
    public <T> T get(String cacheKey, String filePath, Class<T> clazz) {
        String cacheFilePath = getCacheFilePath(cacheKey, filePath);
        try {
            if (isS3ObjectNotExists(cacheFilePath)) {
                return null;
            } else {
                GetObjectRequest request = new GetObjectRequest(getBucketName(), cacheFilePath);
                try (S3Object object = getS3Client().getObject(request)) {

                    final ObjectMetadata objectMetadata = object.getObjectMetadata();
                    if (objectMetadata != null) {
                        final Date lastModified = objectMetadata.getLastModified();
                        if (lastModified != null) {
                            long expirationTime = lastModified.getTime() + getCacheEvictionThresholdTime();
                            if (System.currentTimeMillis() > expirationTime) {
                                LOGGER.debug("Cache entry expired: cacheKey=" + cacheKey + " filePath=" + filePath);
                                // Delete S3 object
                                getS3Client().deleteObject(getBucketName(), cacheFilePath);
                                return null;
                            }
                        }
                    }
                    if (InputStream.class.equals(clazz)) {
                        return (T) object.getObjectContent();
                    } else if (byte[].class.equals(clazz)) {
                        try (final InputStream inputStream = object.getObjectContent()) {
                            return (T) IOUtils.toByteArray(inputStream);
                        }
                    } else {
                        try (InputStream inputStream = object.getObjectContent()) {
                            final ISerializer serializer = getSerializer();
                            return serializer.deserialize(inputStream, clazz);
                        } catch (Exception e) {
                            LOGGER.error("Can't deserialize local file cache entry: cacheKey=" + cacheKey + ", clazz=" + clazz, e);
                            throw e;
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Exception throws while getting cache entry: filePath={}", filePath, e);
            throw new ViewerUiException(e);
        }
    }

    /**
     * Sets a cached file entry based on the cache key and file path. Serializes the data and stores it in AWS S3.
     * If the cache entry already exists, it will be replaced only if the replaceWhenExists flag is set to true.
     *
     * @param cacheKey The cache key associated with the file entry.
     * @param filePath The path of the file in the cache.
     * @param bytes    The data to store in the cache.
     */
    @Override
    public void set(String cacheKey, String filePath, byte[] bytes) {
        if (bytes == null) {
            return;
        }

        String cacheFilePath = getCacheFilePath(cacheKey, filePath);
        try {
            if (isS3ObjectNotExists(cacheFilePath) || isReplaceWhenExists()) {
                try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
                    final ObjectMetadata objectMetadata = new ObjectMetadata();
                    objectMetadata.setLastModified(new Date());
                    PutObjectRequest request = new PutObjectRequest(getBucketName(), cacheFilePath, inputStream, objectMetadata);
                    getS3Client().putObject(request);
                }
            }
        } catch (com.amazonaws.SdkClientException | IOException e) {
            LOGGER.error("Exception throws while saving byte array to AWS S3 cache: cacheKey=" + cacheKey + ", filePath=" + filePath, e);
            throw new ViewerUiException(e);
        }
    }

    /**
     * Stores a cached file entry based on the cache key and file path.
     * If the cache entry already exists, it will be replaced only if the replaceWhenExists flag is set to true.
     *
     * @param cacheKey    The cache key associated with the file entry.
     * @param filePath    The path of the file in the cache.
     * @param inputStream The input stream containing data to store in the cache.
     */
    @Override
    public void set(String cacheKey, String filePath, InputStream inputStream) {
        if (inputStream == null) {
            return;
        }

        String cacheFilePath = getCacheFilePath(cacheKey, filePath);
        try {
            if (isS3ObjectNotExists(cacheFilePath) || isReplaceWhenExists()) {
                final ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setLastModified(new Date());
                PutObjectRequest request = new PutObjectRequest(getBucketName(), cacheFilePath, inputStream, objectMetadata);
                getS3Client().putObject(request);
            }
        } catch (com.amazonaws.SdkClientException e) {
            LOGGER.error("Exception throws while saving input stream to AWS S3 cache: cacheKey=" + cacheKey + ", filePath=" + filePath, e);
            throw new ViewerUiException(e);
        }
    }

    /**
     * Stores a cached object entry based on the cache key and file path. If the cache entry already exists, it will be replaced only if the replaceWhenExists flag is set to true.
     *
     * @param cacheKey The cache key associated with the object entry.
     * @param filePath The path of the object in the cache.
     * @param value    The data to store in the cache.
     */
    @Override
    public void set(String cacheKey, String filePath, Object value) {
        if (value == null) {
            return;
        }

        String cacheFilePath = getCacheFilePath(cacheKey, filePath);
        try {
            if (isS3ObjectNotExists(cacheFilePath) || isReplaceWhenExists()) {
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    final ISerializer serializer = getSerializer();
                    serializer.serialize(value, outputStream);
                    try (InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
                        final ObjectMetadata objectMetadata = new ObjectMetadata();
                        objectMetadata.setLastModified(new Date());
                        PutObjectRequest request = new PutObjectRequest(getBucketName(), cacheFilePath, inputStream, objectMetadata);
                        getS3Client().putObject(request);
                    }
                }
            }
        } catch (com.amazonaws.SdkClientException | IOException e) {
            LOGGER.error("Exception throws while saving input stream to AWS S3 cache: cacheKey=" + cacheKey + ", filePath=" + filePath, e);
            throw new ViewerUiException(e);
        }
    }

    /**
     * Check if an object with the given key exists in the S3 bucket.
     *
     * @param key The key of the object to check for existence.
     * @return True if the object does not exist in the bucket, false otherwise.
     */
    protected boolean isS3ObjectNotExists(String key) {
        return !getS3Client().doesObjectExist(getBucketName(), key);
    }

    private String getCacheFilePath(String cacheKey, String filePath) {
        if (StringUtils.isNullOrEmpty(cacheKey)) {
            throw new IllegalArgumentException("cacheKey");
        }
        if (StringUtils.isNullOrEmpty(filePath)) {
            throw new IllegalArgumentException("filePath");
        }

        String[] parts = filePath.split("[" + String.join("", FilesExtensions.INVALID_PATH_CHARS) + "]");
        String cacheSubFolder = String.join("_", parts).replace(".", "_");

        final String cacheFilePath = cacheSubFolder + File.separator + cacheKey;

        LOGGER.trace("cacheKey={}, filePath={}, cacheFilePath={}", cacheKey, filePath, cacheFilePath);

        return cacheFilePath;
    }

    /**
     * Get the name of the S3 bucket used for caching.
     *
     * @return The name of the S3 bucket.
     */
    public String getBucketName() {
        return _bucketName;
    }

    /**
     * Get the Amazon S3 client used for AWS S3 operations.
     *
     * @return The Amazon S3 client instance.
     */
    private AmazonS3 getS3Client() {
        return _s3Client;
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
