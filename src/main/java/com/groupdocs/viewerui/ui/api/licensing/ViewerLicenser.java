package com.groupdocs.viewerui.ui.api.licensing;

import com.groupdocs.viewer.License;
import com.groupdocs.viewerui.ui.configuration.ViewerConfig;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ViewerLicenser implements IViewerLicenser {

	private final ViewerConfig _viewerConfig;

	private final Object _lock = new Object();

	private boolean _licenseSet;

	public ViewerLicenser(ViewerConfig viewerConfig) {
		_viewerConfig = viewerConfig;
	}

	@Override
	public void setLicense() {
		boolean isUseEvaluationLic = true;

		if (_licenseSet) {
			return;
		}

		StringBuilder sbErrors = new StringBuilder();

		final String configLicensePath = _viewerConfig.getLicensePath();
		if (configLicensePath != null && !configLicensePath.isEmpty()) {
			isUseEvaluationLic = false;
			try {
				setLicense(configLicensePath);
			}
			catch (Exception ex) {
				sbErrors.append("Check config license path error \n");
				sbErrors.append(ex.getMessage());
				sbErrors.append("\n");
			}

			if (_licenseSet) {
				return;
			}
		}

		String licensePath = System.getenv(LicenseKeys.GROUPDOCSVIEWERUI_LIC_PATH_ENVIRONMENT_VARIABLE_KEY);
		if (licensePath != null && !licensePath.isEmpty()) {
			try {
				setLicense(licensePath);
			}
			catch (Exception ex) {
				sbErrors.append("Check environment variable "
						+ LicenseKeys.GROUPDOCSVIEWERUI_LIC_PATH_ENVIRONMENT_VARIABLE_KEY + " error \n");
				sbErrors.append(ex.getMessage());
				sbErrors.append("\n");
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
			}
			catch (Exception ex) {
				sbErrors.append("Check " + licPath + " error \n");
				sbErrors.append(ex.getMessage());
				sbErrors.append("\n");
			}

			if (_licenseSet) {
				return;
			}
		}
		// #if DEBUG
		// if (!isUseEvaluationLic)
		// {
		// throw new FileNotFoundException(sbErrors.ToString());
		// }
		// #endif
	}

	private void setLicense(String licensePath) {
		synchronized (_lock) {
			if (!_licenseSet) {
				License license = new License();
				if (licensePath.startsWith("http")) {
					try {
						license.setLicense(new URL(licensePath));
					} catch (MalformedURLException e) {
						e.printStackTrace(); // TODO: Add logging
						throw new RuntimeException(e);
					}
				} else {
					license.setLicense(licensePath);
				}

				_licenseSet = true;
			}
		}
	}

}
