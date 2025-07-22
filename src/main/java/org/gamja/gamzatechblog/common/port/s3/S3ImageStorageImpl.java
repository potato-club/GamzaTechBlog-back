package org.gamja.gamzatechblog.common.port.s3;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import lombok.RequiredArgsConstructor;

/**
 * 리팩토링 예정
 */
@Service
@RequiredArgsConstructor
public class S3ImageStorageImpl implements S3ImageStorage {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket-name}")
	private String bucketName;

	@Override
	public String uploadStream(InputStream stream, String originalFileName) {
		String key = UUID.randomUUID() + "_" + originalFileName;
		ObjectMetadata meta = new ObjectMetadata();
		String ext = originalFileName.substring(originalFileName.lastIndexOf('.') + 1)
			.toLowerCase(Locale.ROOT);
		meta.setContentType(getContentType(ext));
		amazonS3.putObject(bucketName, key, stream, meta);
		return amazonS3.getUrl(bucketName, key).toString();
	}

	@Override
	public String uploadFile(MultipartFile file) {
		try (InputStream in = file.getInputStream()) {
			return uploadStream(in, file.getOriginalFilename());
		} catch (IOException e) {
			throw new RuntimeException("S3 업로드 실패 (MultipartFile)", e);
		}
	}

	@Override
	public String uploadFromUrl(String imageUrl) {
		try (InputStream in = new URL(imageUrl).openStream()) {
			String name = Paths.get(new URI(imageUrl).getPath())
				.getFileName().toString();
			return uploadStream(in, name);
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException("S3 업로드 실패 (URL)", e);
		}
	}

	@Override
	public void deleteByUrl(String fileUrl) {
		String bucketUrl = amazonS3.getUrl(bucketName, "").toString();
		String key = fileUrl.substring(bucketUrl.length());
		amazonS3.deleteObject(bucketName, key);
	}

	private String getContentType(String ext) {
		switch (ext) {
			case "jpg":
			case "jpeg":
				return "image/jpeg";
			case "png":
				return "image/png";
			case "gif":
				return "image/gif";
			default:
				return "application/octet-stream";
		}
	}
}
