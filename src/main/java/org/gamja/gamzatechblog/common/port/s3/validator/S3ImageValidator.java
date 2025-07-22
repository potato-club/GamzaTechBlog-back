package org.gamja.gamzatechblog.common.port.s3.validator;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.UUID;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;
import org.springframework.stereotype.Component;

/**
 * 찐~~~ 하게 리팩토링 할 예정 진짜 해라 빨리
 */
@Component
public class S3ImageValidator {

	public void validateStreamAndName(InputStream stream, String fileName) {
		if (stream == null || fileName == null || fileName.isBlank()) {
			throw new BusinessException(
				ErrorCode.PROFILE_IMAGE_UPLOAD_FAILED,
				"업로드할 스트림과 파일 이름은 필수입니다."
			);
		}
	}

	public String generateSafeKey(String originalFileName) {
		String safe = originalFileName
			.replaceAll("[^a-zA-Z0-9._-]", "_")
			.replaceAll("_{2,}", "_")
			.replaceAll("^_+|_+$", "");

		if (safe.isBlank()) {
			safe = "file";
		}
		return UUID.randomUUID() + "_" + safe;
	}

	public String determineContentType(String safeKey) {
		String ext = "";
		int dot = safeKey.lastIndexOf('.');
		if (dot > 0 && dot < safeKey.length() - 1) {
			ext = safeKey.substring(dot + 1).toLowerCase(Locale.ROOT);
		}
		return switch (ext) {
			case "png" -> "image/png";
			case "jpg", "jpeg" -> "image/jpeg";
			case "gif" -> "image/gif";
			default -> "application/octet-stream";
		};
	}

	public URL validateAndParseUrl(String imageUrl) {
		if (imageUrl == null || imageUrl.isBlank()) {
			throw new BusinessException(
				ErrorCode.PROFILE_IMAGE_UPLOAD_FAILED,
				"이미지 URL은 필수입니다."
			);
		}
		try {
			URL url = new URL(imageUrl);
			if (!"https".equalsIgnoreCase(url.getProtocol())) {
				throw new BusinessException(
					ErrorCode.PROFILE_IMAGE_UPLOAD_FAILED,
					"HTTPS 프로토콜만 허용됩니다."
				);
			}
			return url;
		} catch (MalformedURLException e) {
			throw new BusinessException(
				ErrorCode.PROFILE_IMAGE_UPLOAD_FAILED,
				"유효하지 않은 URL입니다: " + imageUrl
			);
		}
	}

	public String extractKey(String bucketUrl, String fileUrl) {
		if (fileUrl == null || fileUrl.isBlank() || !fileUrl.startsWith(bucketUrl)) {
			throw new BusinessException(
				ErrorCode.PROFILE_IMAGE_DELETE_FAILED,
				"유효하지 않은 파일 URL입니다."
			);
		}
		String key = fileUrl.substring(bucketUrl.length());

		if (key.startsWith("/")) {
			key = key.substring(1);
		}
		if (key.isBlank()) {
			throw new BusinessException(
				ErrorCode.PROFILE_IMAGE_DELETE_FAILED,
				"키를 추출할 수 없습니다."
			);
		}
		return key;
	}
}
