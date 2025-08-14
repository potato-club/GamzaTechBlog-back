package org.gamja.gamzatechblog.core.config.http;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MultipartJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

	public MultipartJackson2HttpMessageConverter(ObjectMapper mapper) {
		super(mapper,
			MediaType.APPLICATION_JSON,
			MediaType.APPLICATION_OCTET_STREAM);
	}

	@Override
	protected boolean canRead(MediaType mediaType) {
		return MediaType.MULTIPART_FORM_DATA.includes(mediaType)
			|| MediaType.APPLICATION_JSON.includes(mediaType)
			|| MediaType.APPLICATION_OCTET_STREAM.includes(mediaType);
	}

	@Override
	protected boolean canWrite(MediaType mediaType) {
		return false;
	}
}
