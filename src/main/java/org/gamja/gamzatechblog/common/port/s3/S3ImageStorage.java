package org.gamja.gamzatechblog.common.port.s3;

import java.io.InputStream;

public interface S3ImageStorage {
	String upload(InputStream stream, String originalFileName);

	void delete(String url);
}
