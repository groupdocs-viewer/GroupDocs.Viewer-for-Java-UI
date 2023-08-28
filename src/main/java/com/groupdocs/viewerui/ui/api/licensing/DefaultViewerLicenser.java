package com.groupdocs.viewerui.ui.api.licensing;

import com.groupdocs.viewer.License;
import com.groupdocs.viewerui.exception.ViewerUiException;
import com.groupdocs.viewerui.ui.configuration.ViewerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class DefaultViewerLicenser implements ViewerLicenser {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultViewerLicenser.class);

	private final ViewerConfig _viewerConfig;

	private final Object _lock = new Object();

	private boolean _licenseSet;

	public DefaultViewerLicenser(ViewerConfig viewerConfig) {
		_viewerConfig = viewerConfig;
	}

	@Override
	public void setLicense() {

		if (_licenseSet) {
			return;
		}

		final String configLicensePath = _viewerConfig.getLicensePath();
		if (configLicensePath != null && !configLicensePath.isEmpty()) {
			try {
				setLicense(configLicensePath);
			} catch (Exception e) {
				LOGGER.error("Exception throws while setting license via file path: Check config license path", e);
			}

			if (_licenseSet) {
				return;
			}
		}

		String licensePath = System.getenv(LicenseKeys.GROUPDOCSVIEWERUI_LIC_PATH_ENVIRONMENT_VARIABLE_KEY);
		if (licensePath != null && !licensePath.isEmpty()) {
			try {
				setLicense(licensePath);
			} catch (Exception e) {
				LOGGER.error("Exception throws while setting license via file path: Check environment variable " +
							 LicenseKeys.GROUPDOCSVIEWERUI_LIC_PATH_ENVIRONMENT_VARIABLE_KEY, e);
			}

			if (_licenseSet) {
				return;
			}
		}

		List<String> licFileNames = Arrays.asList(
				LicenseKeys.GROUPDOCSVIEWERUI_LIC_FILE_DEFAULT_NAME,
				LicenseKeys.GROUPDOCSVIEWERUI_TEMPORARY_LIC_FILE_DEFAULT_NAME);

		for (String licFileName : licFileNames) {
			String licPath = Paths.get(".").resolve(licFileName).toString();
			try {
				setLicense(licPath);
			} catch (Exception e) {
				LOGGER.error("Exception throws while setting license via file path: Check default license files names - '{}' or '{}'",
						LicenseKeys.GROUPDOCSVIEWERUI_LIC_FILE_DEFAULT_NAME, LicenseKeys.GROUPDOCSVIEWERUI_TEMPORARY_LIC_FILE_DEFAULT_NAME, e);
			}

			if (_licenseSet) {
				return;
			}
		}
	}

	private void setLicense(String licensePath) {
		synchronized (_lock) {
			if (!_licenseSet) {
				License license = new License();
				if (licensePath.startsWith("http")) {
					try {
						license.setLicense(new URL(licensePath));
					} catch (MalformedURLException e) {
						LOGGER.error("Exception throws while setting license from URL", e);
					}
				} else {
					license.setLicense(licensePath);
				}

				_licenseSet = true;
			}
		}
	}
}
