package org.gamja.gamzatechblog.common.port.s3;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface S3ImageStorage {

	String uploadStream(InputStream stream, String filename, String prefix);

	String uploadFile(MultipartFile file, String prefix);

	String uploadFromUrl(String imageUrl, String prefix);

	void deleteByUrl(String fileUrl);
}
