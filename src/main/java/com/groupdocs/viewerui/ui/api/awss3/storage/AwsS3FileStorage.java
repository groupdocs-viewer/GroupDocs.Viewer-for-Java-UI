package com.groupdocs.viewerui.ui.api.awss3.storage;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.StringUtils;
import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.api.awss3.AwsS3Options;
import com.groupdocs.viewerui.ui.core.IFileStorage;
import com.groupdocs.viewerui.ui.core.entities.FileSystemEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AwsS3FileStorage implements IFileStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger(AwsS3FileStorage.class);

    private final AmazonS3 s3Client;
    private final String bucketName;

    public AwsS3FileStorage(AwsS3Options awsS3Options) {
        if (awsS3Options == null) {
            throw new IllegalArgumentException("awsS3Options");
        }

        if (StringUtils.isNullOrEmpty(awsS3Options.getRegion())) {
            throw new IllegalArgumentException("awsS3Options#getRegion() is null or empty");
        }
        this.bucketName = awsS3Options.getBucketName();

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
        this.s3Client = builder.build();
    }

    private static String getObjectName(String key) {
        String[] parts = key.split("/", -1);
        if (parts.length == 0) {
            return key;
        }
        return parts[parts.length - 1];
    }

    @Override
    public List<FileSystemEntry> listDirsAndFiles(final String folderPath) {
        List<FileSystemEntry> entries = new ArrayList<>();

        try {
            final ListObjectsV2Request listRequest = new ListObjectsV2Request()
                    .withBucketName(bucketName)
                    .withPrefix(folderPath)
                    .withDelimiter("/");

            ListObjectsV2Result result;
            do {
                result = s3Client.listObjectsV2(listRequest);

                // Directories
                result.getCommonPrefixes().stream()
                        .filter(prefix -> !folderPath.equals(trimEndDelimiter(prefix))) // Skip itself
                        .forEach((prefix) -> entries.add(FileSystemEntry.directory(trimEndDelimiter(prefix), trimEndDelimiter(prefix), 0L)));
                // Files
                result.getObjectSummaries().forEach((summary) -> entries.add(FileSystemEntry.file(getObjectName(summary.getKey()), summary.getKey(), summary.getSize())));

                // If the response is truncated, set the request ContinuationToken
                // from the NextContinuationToken property of the response.
                listRequest.setContinuationToken(result.getNextContinuationToken());
            } while (result.getNextContinuationToken() != null);

        } catch (AmazonServiceException e) {
            LOGGER.error("Exception throws while listing dirs and files: folderPath={}", folderPath, e);
            throw new ViewerUiException(e);
        }

        return entries;
    }

    private String trimEndDelimiter(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return "";
        } else if (prefix.endsWith("/")) {
            return prefix.substring(0, prefix.length() - 1);
        } else {
            return prefix;
        }
    }

    @Override
    public byte[] readFile(String filePath) {
        GetObjectRequest request = new GetObjectRequest(bucketName, filePath);
        try (S3Object object = s3Client.getObject(request)) {
            return IOUtils.toByteArray(object.getObjectContent());
        } catch (AmazonServiceException | IOException e) {
            LOGGER.error("Exception throws while reading files: filePath={}", filePath, e);
            throw new ViewerUiException(e);
        }
    }

    @Override
    public String writeFile(String fileName, byte[] bytes, boolean rewrite) {
        try (final ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes)) {
            String newFileName = rewrite ? fileName : getFreeFileName(fileName);

            PutObjectRequest request = new PutObjectRequest(bucketName, newFileName, arrayInputStream, new ObjectMetadata());
            s3Client.putObject(request);
            return newFileName;
        } catch (AmazonServiceException e) {
            // Handle S3 exceptions
            if (!rewrite && e.getStatusCode() == 409) {
                LOGGER.warn("Document already exist: fileName={}", fileName, e);
                return null;
            } else {
                LOGGER.error("Exception throws while writing a file: fileName={}", fileName, e);
                throw new ViewerUiException(e);
            }
        } catch (IOException e) {
            LOGGER.error("Exception throws while writing a file: fileName={}", fileName, e);
            throw new ViewerUiException(e);
        }
    }

    public String getFreeFileName(String filePath) {
        final Path filePathObj = Paths.get(filePath);
        String dirPath = filePathObj.getParent().toString();

        List<FileSystemEntry> dirFiles = listDirsAndFiles(dirPath)
                .stream()
                .filter(entry -> !entry.isDirectory())
                .collect(Collectors.toList());

        if (dirFiles.stream().noneMatch(entry -> entry.getFilePath().equals(filePath))) {
            return filePath;
        }

        String fileNameWithoutExtension = filePathObj.getFileName().toString();
        int number = 1;
        String[] fileNameCandidate = new String[]{""};

        do {
            String newFileName = String.format("%s (%d)", fileNameWithoutExtension, number);
            fileNameCandidate[0] = filePath.replace(fileNameWithoutExtension, newFileName);
            ++number;
        } while (dirFiles.stream().anyMatch(entry -> entry.getFilePath().equals(fileNameCandidate[0])));

        return fileNameCandidate[0];
    }
}
