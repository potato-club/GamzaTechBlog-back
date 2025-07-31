package org.gamja.gamzatechblog.common.port.s3.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TempImageCleanupScheduler {

	private final AmazonS3 s3;

	@Value("${cloud.aws.s3.bucket-name}")
	private String bucketName;

	@Value("${app.temp-image-cleanup.hours:24}")
	private long cleanupThresholdHours;

	@Scheduled(cron = "0 0 3 * * *")
	public void cleanupOldTempImages() {
		log.info("▶ 임시 이미지 정리 시작 ({}시간 이전 tmp_images/*)", cleanupThresholdHours);
		try {
			Date cutoff = Date.from(
				Instant.now().minus(cleanupThresholdHours, ChronoUnit.HOURS)
			);

			ListObjectsV2Request req = new ListObjectsV2Request()
				.withBucketName(bucketName)
				.withPrefix("tmp_images/")
				.withMaxKeys(1000);

			ListObjectsV2Result result;
			int deleted = 0;

			do {
				result = s3.listObjectsV2(req);
				for (S3ObjectSummary obj : result.getObjectSummaries()) {
					if (obj.getLastModified().before(cutoff)) {
						try {
							s3.deleteObject(bucketName, obj.getKey());
							deleted++;
							log.debug("  삭제: {}", obj.getKey());
						} catch (Exception ex) {
							log.error("  삭제 실패: {}", obj.getKey(), ex);
						}
					}
				}
				req.setContinuationToken(result.getNextContinuationToken());
			} while (result.isTruncated());

			log.info("▶ 임시 이미지 정리 완료 (삭제된 파일 수: {})", deleted);

		} catch (Exception e) {
			log.error("▶ 임시 이미지 정리 중 예외 발생", e);
		}
	}
}
