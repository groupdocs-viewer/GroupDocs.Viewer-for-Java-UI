package com.groupdocs.viewerui.ui.api.azure.storage;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.api.azure.AzureBlobOptions;
import com.groupdocs.viewerui.ui.core.IFileStorage;
import com.groupdocs.viewerui.ui.core.entities.FileSystemEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class implements the IFileStorage interface to interact with Azure Blob Storage.
 */
public class AzureBlobFileStorage implements IFileStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger(AzureBlobFileStorage.class);

    private final BlobContainerClient _blobContainerClient;

    /**
     * Constructs an instance of AzureBlobFileStorage with the provided AzureBlobOptions.
     *
     * @param azureBlobOptions The options for configuring the Azure Blob Storage client.
     */
    public AzureBlobFileStorage(AzureBlobOptions azureBlobOptions) {
        if (azureBlobOptions == null) {
            throw new IllegalArgumentException("azureBlobOptions");
        }

        final BlobContainerClientBuilder builder = new BlobContainerClientBuilder()
                .containerName(azureBlobOptions.getContainerName())
                .connectionString(
                        "DefaultEndpointsProtocol=" + azureBlobOptions.getDefaultEndpointsProtocol()
                                + ";AccountName=" + azureBlobOptions.getAccountName()
                                + ";AccountKey=" + azureBlobOptions.getAccountKey()
                                + ";EndpointSuffix=" + azureBlobOptions.getEndpointSuffix())
                .clientOptions(azureBlobOptions.getClientOptions());

        this._blobContainerClient = builder.buildClient();
    }

    /**
     * Lists directories and files within the specified folder path in Azure Blob Storage.
     *
     * @param folderPath The path of the folder to list contents from. If empty, it will list directly from the container root.
     * @return A list of FileSystemEntry objects representing the entries in the folder.
     */
    @Override
    public List<FileSystemEntry> listDirsAndFiles(String folderPath) {
        List<FileSystemEntry> entries = new ArrayList<>();
        try {
            final PagedIterable<BlobItem> blobItems = getBlobContainerClient().listBlobsByHierarchy(folderPath.isEmpty() ? folderPath : folderPath + "/");
            blobItems.forEach(item -> {
                if (item.isPrefix()) {
                    final String itemPath = item.getName();
                    final String name = itemPath.endsWith("/") ? itemPath.substring(0, itemPath.length() - 1) : itemPath;
                    entries.add(FileSystemEntry.directory(name, name, 0L));
                } else {
                    final String itemPath = item.getName();
                    final String fileName = Paths.get(itemPath).getFileName().toString();
                    entries.add(FileSystemEntry.file(fileName, itemPath, item.getProperties().getContentLength()));
                }
            });
        } catch (Exception e) {
            LOGGER.error("Exception throws while getting cache entry: folderPath={}", folderPath, e);
            throw new ViewerUiException(e);
        }
        return entries;
    }

    /**
     * Reads the content of a file from Azure Blob Storage and returns it as a byte array.
     *
     * @param filePath The path to the file in the storage.
     * @return A byte array containing the file's contents.
     */
    @Override
    public byte[] readFile(String filePath) {
        try {
            final BlobClient blobClient = getBlobContainerClient().getBlobClient(filePath);

            final BinaryData binaryData = blobClient.downloadContent();

            return binaryData.toBytes();
        } catch (Exception e) {
            LOGGER.error("Exception throws while getting cache entry: filePath={}", filePath, e);
            throw new ViewerUiException(e);
        }
    }

    /**
     * Writes a byte array to Azure Blob Storage and returns the file name used in storage.
     *
     * @param fileName The name of the file to be stored.
     * @param bytes    The byte array containing the data to be written.
     * @param rewrite  Whether to overwrite an existing file with the same name (true) or generate a new unique filename (false).
     * @return The path where the file is stored in Azure Blob Storage.
     */
    @Override
    public String writeFile(String fileName, byte[] bytes, boolean rewrite) {
        if (fileName == null || bytes == null) {
            return null;
        }

        try {
            String newFileName = rewrite ? fileName : getFreeFileName(fileName);
            final BlobClient blobClient = getBlobContainerClient().getBlobClient(newFileName);
            blobClient.upload(BinaryData.fromBytes(bytes), rewrite);
            return newFileName;
        } catch (Exception e) {
            LOGGER.error("Exception throws while saving byte array to Azure Blob Storage cache: fileName=" + fileName, e);
            throw new ViewerUiException(e);
        }
    }

    /**
     * Generates a free file name by appending a number suffix if the specified filename already exists in the storage.
     *
     * @param filePath The original file path to be checked and potentially modified.
     * @return A unique file name that does not conflict with existing files in the storage.
     */
    private String getFreeFileName(String filePath) {
        final Path filePathObj = Paths.get(filePath);
        final Path parent = filePathObj.getParent();
        String dirPath = parent == null ? "/" : parent.toString();

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

    private BlobContainerClient getBlobContainerClient() {
        return _blobContainerClient;
    }
}
