package com.groupdocs.viewerui.ui.core.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Page {

	private final List<PageResource> _resources = new ArrayList<>();

	private int _pageNumber;

	private byte[] _data;

	protected Page(int pageNumber, byte[] data) {
		_pageNumber = pageNumber;
		_data = data;
	}

	protected Page(int pageNumber, byte[] data, List<PageResource> resources) {
		_pageNumber = pageNumber;
		_data = data;
		_resources.addAll(resources);
	}

	public List<PageResource> getResources() {
		return _resources;
	}

	public int getPageNumber() {
		return _pageNumber;
	}

	public byte[] getData() {
		return _data;
	}

	public void setData(byte[] data) {
		this._data = data;
	}

	public abstract String getContent();

	public abstract void setContent(String content);

	public void addResource(PageResource pageResource) {
		_resources.add(pageResource);
	}

	public PageResource getResource(String resourceName) {
		final Optional<PageResource> firstOptional = _resources.stream()
			.filter(pageResource -> resourceName.equals(pageResource.getResourceName()))
			.findFirst();
		return firstOptional.orElse(null);
	}

}
