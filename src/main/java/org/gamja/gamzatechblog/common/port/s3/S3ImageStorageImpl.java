package org.gamja.gamzatechblog.common.port.s3;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.gamja.gamzatechblog.common.port.s3.validator.S3ImageValidator;
import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class S3ImageStorageImpl implements S3ImageStorage {

	private final AmazonS3 amazonS3;

	private final S3ImageValidator validator;
	@Value("${cloud.aws.s3.bucket-name}")
	private String bucketName;

	@Override
	public String uploadStream(InputStream stream, String originalFileName) {
		validator.validateStreamAndName(stream, originalFileName);

		String key = validator.generateSafeKey(originalFileName);
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentType(validator.determineContentType(key));

		amazonS3.putObject(bucketName, key, stream, meta);
		return amazonS3.getUrl(bucketName, key).toString();
	}

	@Override
	public String uploadFile(MultipartFile file) {
		String filename = file.getOriginalFilename();
		if (filename == null || filename.isBlank()) {
			filename = "unnamed_" + System.currentTimeMillis();
		}
		try (InputStream in = file.getInputStream()) {
			return uploadStream(in, filename);
		} catch (IOException e) {
			throw new BusinessException(
				ErrorCode.PROFILE_IMAGE_UPLOAD_FAILED,
				"파일 읽기 중 오류가 발생했습니다."
			);
		}
	}

	@Override
	public String uploadFromUrl(String imageUrl) {
		URL url = validator.validateAndParseUrl(imageUrl);
		try (InputStream in = url.openStream()) {
			String name = URI.create(imageUrl).getPath();
			name = name.substring(name.lastIndexOf('/') + 1);
			if (name.isBlank()) {
				name = "image_" + System.currentTimeMillis();
			}
			return uploadStream(in, name);
		} catch (IOException e) {
			throw new BusinessException(
				ErrorCode.PROFILE_IMAGE_UPLOAD_FAILED,
				"URL 업로드 중 I/O 오류가 발생했습니다."
			);
		}
	}

	@Override
	public void deleteByUrl(String fileUrl) {
		String bucketUrl = amazonS3.getUrl(bucketName, "").toString();
		String key = validator.extractKey(bucketUrl, fileUrl);
		try {
			amazonS3.deleteObject(bucketName, key);
		} catch (SdkClientException e) {
			throw new BusinessException(
				ErrorCode.PROFILE_IMAGE_DELETE_FAILED,
				"파일 삭제 중 오류가 발생했습니다."
			);
		}
	}
}
