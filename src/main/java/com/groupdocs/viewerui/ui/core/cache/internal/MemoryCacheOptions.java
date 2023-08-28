package com.groupdocs.viewerui.ui.core.cache.internal;

public class MemoryCacheOptions {
    private long _expirationScanFrequency = 60 * 1000L;

    public MemoryCacheOptions() {
    }

    public MemoryCacheOptions(long expirationScanFrequency) {
        this._expirationScanFrequency = expirationScanFrequency;
    }

    public long getExpirationScanFrequency() {
        return _expirationScanFrequency;
    }

    public void setExpirationScanFrequency(long expirationScanFrequency) {
        this._expirationScanFrequency = expirationScanFrequency;
    }
}
