package org.gamja.gamzatechblog.common.port.s3.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TempImageCleanupScheduler {

	private final AmazonS3 s3;
	@Value("${cloud.aws.s3.bucket-name}")
	private String bucketName;

	@Scheduled(cron = "0 0 3 * * *")
	public void cleanupOldTempImages() {
		ObjectListing objs = s3.listObjects(bucketName, "tmp_images/");
		objs.getObjectSummaries().stream()
			.filter(obj -> {
				return obj.getLastModified()
					.before(Date.from(Instant.now().minus(24, ChronoUnit.HOURS)));
			})
			.map(S3ObjectSummary::getKey)
			.forEach(key -> {
				s3.deleteObject(bucketName, key);
			});
	}
}
