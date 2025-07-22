package org.gamja.gamzatechblog.common.port.s3;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface S3ImageStorage {

	String uploadStream(InputStream stream, String filename);

	String uploadFile(MultipartFile file);

	String uploadFromUrl(String imageUrl);

	void deleteByUrl(String fileUrl);
}
