package com.groupdocs.viewerui.ui.configuration;

/**
 * This class contains options for internal objects caching.
 */
public class InternalCacheOptions {

	public static final InternalCacheOptions CACHE_FOR_FIVE_MINUTES = new InternalCacheOptions();

	static {
		CACHE_FOR_FIVE_MINUTES.setCacheEnabled(true);
		CACHE_FOR_FIVE_MINUTES.setCacheEntryExpirationTimeoutMinutes(5);
	}

	private boolean _isCacheEnabled;

	private int _cacheEntryExpirationTimeoutMinutes;

	public boolean isCacheEnabled() {
		return _isCacheEnabled;
	}

	public void setCacheEnabled(boolean isCacheEnabled) {
		_isCacheEnabled = isCacheEnabled;
	}

	public boolean isCacheDisabled() {
		return !isCacheEnabled();
	}

	public int getCacheEntryExpirationTimeoutMinutes() {
		return _cacheEntryExpirationTimeoutMinutes;
	}

	public void setCacheEntryExpirationTimeoutMinutes(int _cacheEntryExpirationTimeoutMinutes) {
		this._cacheEntryExpirationTimeoutMinutes = _cacheEntryExpirationTimeoutMinutes;
	}

	/**
	 * Turn of caching. By default caching instanceof enabled.
	 * @return This instance.
	 */
	public InternalCacheOptions disableInternalCache() {
		_isCacheEnabled = false;
		return this;
	}

	/**
	 * Set the sliding expiration timeout of each cache entry in minutes. The default
	 * value instanceof 5 minutes.
	 * @param cacheEntryExpirationTimeoutMinutes The expiration timeout in minutes.
	 * @return This instance.
	 */
	public InternalCacheOptions cacheEntryExpirationTimeoutMinutes(int cacheEntryExpirationTimeoutMinutes) {
		_cacheEntryExpirationTimeoutMinutes = cacheEntryExpirationTimeoutMinutes;
		return this;
	}

}
