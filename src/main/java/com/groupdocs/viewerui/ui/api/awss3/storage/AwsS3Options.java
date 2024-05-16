package com.groupdocs.viewerui.ui.api.awss3.storage;

import com.amazonaws.ClientConfiguration;

public class AwsS3Options {
    private String _region;
    private String _accessKey;
    private String _secretKey;
    private final ClientConfiguration _clientConfiguration;
    private String _bucketName;

    public AwsS3Options() {
        _clientConfiguration = new ClientConfiguration();
    }

    public AwsS3Options(ClientConfiguration clientConfiguration) {
        _clientConfiguration = clientConfiguration;
    }

    public String getRegion() {
        return _region;
    }

    public void setRegion(String region) {
        this._region = region;
    }

    public String getAccessKey() {
        return _accessKey;
    }

    public void setAccessKey(String accessKey) {
        this._accessKey = accessKey;
    }

    public String getSecretKey() {
        return _secretKey;
    }

    public void setSecretKey(String secretKey) {
        this._secretKey = secretKey;
    }

    public ClientConfiguration getClientConfiguration() {
        return _clientConfiguration;
    }

    public String getBucketName() {
        return _bucketName;
    }

    public void setBucketName(String bucketName) {
        this._bucketName = bucketName;
    }
}
