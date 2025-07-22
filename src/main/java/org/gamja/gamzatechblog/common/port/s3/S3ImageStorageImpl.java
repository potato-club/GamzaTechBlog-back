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
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
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
		// 1) 로깅: bucketUrl, fileUrl, 추출된 키 확인
		log.info("S3 삭제 요청 → bucketUrl='{}', fileUrl='{}'", bucketUrl, fileUrl);

		String key = validator.extractKey(bucketUrl, fileUrl);
		log.info("추출된 S3 key='{}'", key);

		try {
			amazonS3.deleteObject(bucketName, key);
			log.info("S3 객체 삭제 성공 → bucket='{}', key='{}'", bucketName, key);
		} catch (SdkClientException e) {
			// 2) 예외 메시지·원인까지 함께 로깅
			log.error("S3 삭제 실패 → bucket='{}', key='{}', message='{}'",
				bucketName, key, e.getMessage(), e);
			throw new BusinessException(
				ErrorCode.PROFILE_IMAGE_DELETE_FAILED,
				"파일 삭제 중 오류가 발생했습니다: " + e.getMessage()
			);
		}
	}

}
