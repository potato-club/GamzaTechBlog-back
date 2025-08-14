package org.gamja.gamzatechblog.core.config.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {

	@Value("${cloud.aws.s3.region}")
	private String region;

	@Value("${cloud.aws.s3.bucket-name}")
	private String bucketName;

	@Value("${cloud.aws.s3.access-key}")
	private String accessKey;

	@Value("${cloud.aws.s3.secret-key}")
	private String secretKey;

	@Bean
	public AmazonS3 amazonS3() {
		BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
		return AmazonS3ClientBuilder.standard()
			.withRegion(region)
			.withCredentials(new AWSStaticCredentialsProvider(creds))
			.build();
	}
}

