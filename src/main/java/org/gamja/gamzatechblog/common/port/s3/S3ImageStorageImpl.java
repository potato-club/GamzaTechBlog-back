package org.gamja.gamzatechblog.common.port.s3;

import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import lombok.RequiredArgsConstructor;

/**
 * ErrorCode와 Exception 커스텀해 예외 처리 할 예정입니다.
 */
@Service
@RequiredArgsConstructor
public class S3ImageStorageImpl implements S3ImageStorage {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket-name}")
	private String bucketName;

	@Override
	public String upload(InputStream stream, String originalFileName) {
		if (stream == null) {
			throw new IllegalArgumentException("업로드 스트림이 null 입니다");
		}
		if (originalFileName == null || originalFileName.trim().isEmpty()) {
			throw new IllegalArgumentException("파일명이 비어 있습니다");
		}
		try {
			String key = UUID.randomUUID() + "_" + originalFileName;
			ObjectMetadata metadata = new ObjectMetadata();
			String contentType = getContentType(originalFileName);
			if (contentType != null) {
				metadata.setContentType(contentType);
			}
			amazonS3.putObject(bucketName, key, stream, metadata);
			return amazonS3.getUrl(bucketName, key).toString();
		} catch (Exception e) {
			throw new RuntimeException("S3 파일 업로드 실패: " + originalFileName, e);
		}
	}

	@Override
	public void delete(String url) {
		if (url == null || url.isEmpty()) {
			throw new IllegalArgumentException("URL이 비어 있습니다");
		}
		try {
			String key = extractKeyFromUrl(url);
			amazonS3.deleteObject(bucketName, key);
		} catch (Exception e) {
			throw new RuntimeException("S3 파일 삭제 실패: " + url, e);
		}
	}

	private String getContentType(String fileName) {
		String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
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

	private String extractKeyFromUrl(String url) {
		String bucketUrl = amazonS3.getUrl(bucketName, "").toString();
		if (!url.startsWith(bucketUrl)) {
			throw new IllegalArgumentException("버킷에 속하지 않는 URL 입니다");
		}
		return url.substring(bucketUrl.length());
	}
}
