package com.groupdocs.viewerui.ui.core.serialize;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.groupdocs.viewerui.exception.ViewerUiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;

public class JacksonJsonSerializer implements ISerializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(JacksonJsonSerializer.class);

	private static final ObjectMapper MAPPER = new ObjectMapper();

	static {
		MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


		SimpleModule module = new SimpleModule(Version.unknownVersion());

		SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();

		module.setAbstractTypes(resolver);

		MAPPER.registerModule(module);
	}

	@Override
	public void serialize(Object value, OutputStream outputStream) {
		try {
			MAPPER.writerWithDefaultPrettyPrinter().writeValue(outputStream, value);
		}
		catch (Exception e) {
			LOGGER.error("Exception throws while serializing object: value={}", value, e);
			throw new ViewerUiException(e);
		}
	}

	@Override
	public <T> T deserialize(InputStream inputStream, Class<T> clazz) {
		try {
			return MAPPER.readValue(inputStream, clazz);
		}
		catch (Exception e) {
			LOGGER.error("Exception throws while deserializing object: clazz={}", clazz, e);
			throw new ViewerUiException(e);
		}
	}

}
