package com.groupdocs.viewerui.ui.core.serialize;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.InputStream;
import java.io.OutputStream;

public class JacksonJsonSerializer implements ISerializer {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	static {
		MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


		SimpleModule module = new SimpleModule(Version.unknownVersion());

		SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
		// resolver.addMapping(ArchiveViewInfo.class, ArchiveViewInfoModel.class);

		module.setAbstractTypes(resolver);

		MAPPER.registerModule(module);
	}

	@Override
	public void serialize(Object value, OutputStream outputStream) {
		try {
			MAPPER.writerWithDefaultPrettyPrinter().writeValue(outputStream, value);
		}
		catch (Exception e) {
			e.printStackTrace(); // TODO: Add logging
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T deserialize(InputStream inputStream, Class<T> clazz) {
		try {
			return MAPPER.readValue(inputStream, clazz);
		}
		catch (Exception e) {
			e.printStackTrace(); // TODO: Add logging
			throw new RuntimeException(e);
		}
	}

}
