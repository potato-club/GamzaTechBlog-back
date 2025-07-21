package org.gamja.gamzatechblog.common.port.s3;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

@Service
public class S3ImageStorageImpl implements S3ImageStorage {

	private final AmazonS3 amazonS3;
	private final String bucketName;

	public S3ImageStorageImpl(AmazonS3 amazonS3,
		@Value("${cloud.aws.s3.bucket-name}") String bucketName) {
		this.amazonS3 = amazonS3;
		this.bucketName = bucketName;
	}

	@Override
	public String upload(InputStream stream, String originalFileName) {
		String key = UUID.randomUUID() + "_" + originalFileName;
		amazonS3.putObject(bucketName, key, stream, new ObjectMetadata());
		return amazonS3.getUrl(bucketName, key).toString();
	}

	@Override
	public void delete(String url) {
		String key = url.substring(url.lastIndexOf('/') + 1);
		amazonS3.deleteObject(bucketName, key);
	}
}
