package org.gamja.gamzatechblog.domain.postimage.service.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PostImageServiceUtil {

	@Value("${cloud.aws.s3.bucket-name}")
	private String bucketName;

	@Value("${cloud.aws.s3.region}")
	private String region;

	private static final Pattern MD_IMG =
		Pattern.compile("!\\[[^\\]]*\\]\\(([^)]+)\\)");

	private String getBucketDomain() {
		return String.format("%s.s3.%s.amazonaws.com", bucketName, region);
	}

	public List<String> extractImageUrls(String content) {
		Matcher matcher = MD_IMG.matcher(content);
		List<String> urls = new ArrayList<>();
		String bucketDomain = getBucketDomain();
		while (matcher.find()) {
			String url = matcher.group(1).trim();
			if (url.startsWith("data:image/")) {
				urls.add(url);
			} else if ((url.startsWith("http://") || url.startsWith("https://"))
				&& url.contains(bucketDomain)) {
				urls.add(url);
			}
		}
		return urls;
	}
}
