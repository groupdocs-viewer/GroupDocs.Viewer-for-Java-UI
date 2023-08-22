package com.groupdocs.viewerui.ui.core.serialize;

import java.io.InputStream;
import java.io.OutputStream;

public interface ISerializer {

	void serialize(Object value, OutputStream outputStream);

	<T> T deserialize(InputStream inputStream, Class<T> clazz);

}
