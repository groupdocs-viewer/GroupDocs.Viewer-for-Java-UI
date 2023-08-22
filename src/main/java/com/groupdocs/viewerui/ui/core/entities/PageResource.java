package com.groupdocs.viewerui.ui.core.entities;

public class PageResource {

	private String _resourceName;

	private byte[] _data;

	public PageResource(String resourceName, byte[] data) {
		_resourceName = resourceName;
		_data = data;
	}

	public String getResourceName() {
		return _resourceName;
	}

	public byte[] getData() {
		return _data;
	}

}
