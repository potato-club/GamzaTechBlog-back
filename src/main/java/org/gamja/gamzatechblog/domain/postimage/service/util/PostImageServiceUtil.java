package org.gamja.gamzatechblog.domain.postimage.service.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gamja.gamzatechblog.common.port.s3.S3ImageStorage;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostImageServiceUtil {
	private final S3ImageStorage s3ImageStorage;
	private static final Logger log = LoggerFactory.getLogger(PostImageServiceUtil.class);

	private static final Pattern DATA_IMG =
		Pattern.compile("!\\[[^\\]]*\\]\\((data:image/[^)]+)\\)");
	private static final Pattern EXTERNAL_IMG =
		Pattern.compile("!\\[[^\\]]*\\]\\((https?://[^)]+)\\)");
	private static final Pattern MD_IMG =
		Pattern.compile("!\\[[^\\]]*\\]\\(([^)]+)\\)");

	public String replaceAndUploadAllImages(Post post, String content) {
		Matcher dataMatcher = DATA_IMG.matcher(content);
		StringBuilder sb = new StringBuilder();
		while (dataMatcher.find()) {
			String dataUrl = dataMatcher.group(1);
			String mime = dataUrl.substring(dataUrl.indexOf(':') + 1, dataUrl.indexOf(';'));
			String ext = mime.substring(mime.indexOf('/') + 1);
			String base64 = dataUrl.substring(dataUrl.indexOf(',') + 1);

			try {
				byte[] bytes = Base64.getDecoder().decode(base64);
				InputStream in = new ByteArrayInputStream(bytes);
				String filename = String.format("post-%d-%d.%s",
					post.getId(), System.currentTimeMillis(), ext);
				String s3Url = s3ImageStorage.uploadStream(in, filename);
				dataMatcher.appendReplacement(sb, "![](" + s3Url + ")");
			} catch (Exception e) {
				log.warn("Base64 이미지 처리 실패 postId={} mime={} error={}",
					post.getId(), mime, e.getMessage());
				dataMatcher.appendReplacement(sb, dataMatcher.group(0));
			}
		}
		dataMatcher.appendTail(sb);

		String interim = sb.toString();
		Matcher extMatcher = EXTERNAL_IMG.matcher(interim);
		sb = new StringBuilder();
		while (extMatcher.find()) {
			String url = extMatcher.group(1);
			try {
				String s3Url = s3ImageStorage.uploadFromUrl(url);
				extMatcher.appendReplacement(sb, "![](" + s3Url + ")");
			} catch (Exception e) {
				log.warn("외부 이미지 처리 실패 postId={} url={} error={}",
					post.getId(), url, e.getMessage());
				extMatcher.appendReplacement(sb, extMatcher.group(0));
			}
		}
		extMatcher.appendTail(sb);

		return sb.toString();
	}

	public List<String> extractImageUrls(String content) {
		Matcher matcher = MD_IMG.matcher(content);
		List<String> urls = new ArrayList<>();
		while (matcher.find()) {
			urls.add(matcher.group(1));
		}
		return urls;
	}
}
