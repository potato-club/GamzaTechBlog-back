package org.gamja.gamzatechblog.domain.post.util;

import java.util.List;

import org.gamja.gamzatechblog.core.auth.oauth.client.GithubApiClient;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostUtil {
	private final GithubApiClient githubApiClient;

	private static final String REPO_NAME = "GamzaTechBlog";
	private static final String BASE_DIR = "PotatoStudy";
	private static final String FALLBACK_TAG = "etc";
	private static final String UNTITLED = "untitled";

	public String syncToGitHub(String token, String oldTitle, List<String> oldTags, Post post, List<String> tags,
		String action, String commitMessage) {
		String owner = post.getUser().getNickname();
		githubApiClient.createRepositoryIfNotExists(token, REPO_NAME, owner);

		String path = buildPostPath(post.getId(), post.getTitle(), tags);
		String msg = messageOrDefault(commitMessage, action, primaryTag(tags), post.getTitle());

		if (oldTitle != null && oldTags != null) {
			String oldPath = buildPostPath(post.getId(), oldTitle, oldTags);
			if (!oldPath.equals(path)) {
				String oldFileName = fileName(post.getId(), sanitize(oldTitle));
				githubApiClient.deleteFile(token, owner, REPO_NAME, oldPath, "Delete(old): " + oldFileName);
			}
		}

		if ("Delete".equals(action)) {
			return githubApiClient.deleteFile(token, owner, REPO_NAME, path, msg);
		} else {
			String markdown = buildMarkdownWithFrontmatter(post, tags);
			return githubApiClient.createOrUpdateFile(token, owner, REPO_NAME, path, msg, markdown);
		}
	}

	public String deleteFromGitHub(String token, String owner, long postId, String prevTitle, List<String> prevTags,
		String commitMessage) {
		String path = buildPostPath(postId, prevTitle, prevTags);
		String msg = messageOrDefault(commitMessage, "Delete", primaryTag(prevTags), prevTitle);
		return githubApiClient.deleteFile(token, owner, REPO_NAME, path, msg);
	}

	private String buildPostPath(long postId, String title, List<String> tags) {
		String tag = primaryTag(tags);
		String safeTitle = sanitize(title);
		return BASE_DIR + "/" + tag + "/" + fileName(postId, safeTitle);
	}

	private String fileName(long postId, String safeTitle) {
		return postId + "-" + safeTitle + ".md";
	}

	private String primaryTag(List<String> tags) {
		if (tags == null || tags.isEmpty())
			return FALLBACK_TAG;
		String t = tags.get(0);
		return (t == null || t.isBlank()) ? FALLBACK_TAG : t;
	}

	private String sanitize(String title) {
		if (title == null || title.isBlank())
			return UNTITLED;
		return title.replaceAll("[^\\w가-힣ㄱ-ㅎㅏ-ㅣ]+", "_");
	}

	private String messageOrDefault(String explicit, String action, String tag, String title) {
		if (explicit != null && !explicit.isBlank())
			return explicit;
		return action + ": [" + tag + "] " + title;
	}

	public String stripAllImages(String markdown) {
		if (markdown == null)
			return "";
		return markdown.replaceAll("!\\[[^\\]]*\\]\\([^\\)]*\\)", "");
	}

	public String makeSnippet(String markdown, int length) {
		String noImages = stripAllImages(markdown).trim();
		return (noImages.length() <= length) ? noImages : noImages.substring(0, length) + "...";
	}

	private String buildMarkdownWithFrontmatter(Post post, List<String> tags) {
		return post.getContent();
	}
}
