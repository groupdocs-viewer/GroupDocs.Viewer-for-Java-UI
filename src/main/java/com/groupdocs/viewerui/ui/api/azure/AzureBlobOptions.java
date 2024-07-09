package com.groupdocs.viewerui.ui.api.azure;

import com.azure.core.util.ClientOptions;

/**
 * The AzureBlobOptions class provides options for configuring the connection to an Azure Blob Storage account.
 */
public class AzureBlobOptions {
    private final ClientOptions _clientOptions;
    private String _accountName;
    private String _accountKey;
    private String _containerName;
    private String _defaultEndpointsProtocol = "https";
    private String _endpointSuffix = "core.windows.net";

    /**
     * Constructs a new AzureBlobOptions object with default client options.
     */
    public AzureBlobOptions() {
        _clientOptions = new ClientOptions();
    }

    /**
     * Constructs a new AzureBlobOptions object with the specified client options.
     *
     * @param clientOptions The client options for customizing the Azure Blob Storage client.
     */
    public AzureBlobOptions(ClientOptions clientOptions) {
        this._clientOptions = clientOptions;
    }

    /**
     * Gets the name of the Azure Storage account.
     * <a href="https://docs.microsoft.com/azure/storage/common/storage-configure-connection-string">Configure Azure Storage connection strings</a>
     *
     * @return The account name.
     */
    public String getAccountName() {
        return _accountName;
    }

    /**
     * Sets the name of the Azure Storage account.
     * <a href="https://docs.microsoft.com/azure/storage/common/storage-configure-connection-string">Configure Azure Storage connection strings</a>
     *
     * @param accountName The new account name to set.
     */
    public void setAccountName(String accountName) {
        this._accountName = accountName;
    }

    /**
     * Gets the access key for the Azure Storage account.
     * <a href="https://docs.microsoft.com/azure/storage/common/storage-configure-connection-string">Configure Azure Storage connection strings</a>
     *
     * @return The account key.
     */
    public String getAccountKey() {
        return _accountKey;
    }

    /**
     * Sets the access key for the Azure Storage account.
     * <a href="https://docs.microsoft.com/azure/storage/common/storage-configure-connection-string">Configure Azure Storage connection strings</a>
     *
     * @param accountKey The new account key to set.
     */
    public void setAccountKey(String accountKey) {
        this._accountKey = accountKey;
    }

    /**
     * Gets the name of the container within the storage account.
     *
     * @return The container name.
     */
    public String getContainerName() {
        return _containerName;
    }

    /**
     * Sets the name of the container within the storage account.
     *
     * @param containerName The new container name to set.
     */
    public void setContainerName(String containerName) {
        this._containerName = containerName;
    }

    /**
     * Gets the protocol used to connect to the Azure Storage account (e.g., "https").
     *
     * @return The default endpoints protocol.
     */
    public String getDefaultEndpointsProtocol() {
        return _defaultEndpointsProtocol;
    }

    /**
     * Sets the protocol used to connect to the Azure Storage account.
     *
     * @param defaultEndpointsProtocol The new protocol to set (e.g., "https").
     */
    public void setDefaultEndpointsProtocol(String defaultEndpointsProtocol) {
        this._defaultEndpointsProtocol = defaultEndpointsProtocol;
    }

    /**
     * Gets the client options for customizing the Azure Blob Storage client.
     *
     * @return The ClientOptions object.
     */
    public ClientOptions getClientOptions() {
        return _clientOptions;
    }

    /**
     * Gets the suffix of the endpoint URL.
     *
     * @return The endpoint suffix.
     */
    public String getEndpointSuffix() {
        return _endpointSuffix;
    }

    /**
     * Sets the suffix of the endpoint URL.
     *
     * @param endpointSuffix The endpoint suffix.
     */
    public void setEndpointSuffix(String endpointSuffix) {
        this._endpointSuffix = endpointSuffix;
    }
}
