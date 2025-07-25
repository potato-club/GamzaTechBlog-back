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
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostImageServiceUtil {
	private final S3ImageStorage s3ImageStorage;

	private static final Pattern DATA_IMG =
		Pattern.compile("!\\[[^\\]]*\\]\\((data:image/[^)]+)\\)");

	private static final Pattern EXTERNAL_IMG =
		Pattern.compile("!\\[[^\\]]*\\]\\((https?://[^)]+)\\)");

	private static final Pattern MD_IMG =
		Pattern.compile("!\\[[^\\]]*\\]\\(([^)]+)\\)");

	public String replaceAndUploadAllImages(Post post, String content) {
		Matcher dataM = DATA_IMG.matcher(content);
		StringBuffer stringBuffer = new StringBuffer();
		while (dataM.find()) {
			String dataUrl = dataM.group(1);
			String base64 = dataUrl.substring(dataUrl.indexOf(',') + 1);
			byte[] bytes = Base64.getDecoder().decode(base64);
			InputStream in = new ByteArrayInputStream(bytes);
			String name = "post-" + post.getId() + "-" + System.currentTimeMillis() + ".png";
			String s3Url = s3ImageStorage.uploadStream(in, name);
			dataM.appendReplacement(stringBuffer, "![](" + s3Url + ")");
		}
		dataM.appendTail(stringBuffer);
		String updated = stringBuffer.toString();

		Matcher extM = EXTERNAL_IMG.matcher(updated);
		stringBuffer = new StringBuffer();
		while (extM.find()) {
			String url = extM.group(1);
			String s3Url = s3ImageStorage.uploadFromUrl(url);
			extM.appendReplacement(stringBuffer, "![](" + s3Url + ")");
		}
		extM.appendTail(stringBuffer);

		return stringBuffer.toString();
	}

	public List<String> extractImageUrls(String content) {
		Matcher m = MD_IMG.matcher(content);
		List<String> urls = new ArrayList<>();
		while (m.find()) {
			urls.add(m.group(1));
		}
		return urls;
	}
}
