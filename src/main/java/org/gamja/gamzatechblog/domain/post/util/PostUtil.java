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

	public String syncToGitHub(String token, String oldTitle, List<String> oldTags,
		Post post, List<String> tags, String action, String commitMessage) {
		String repoName = "GamzaTechBlog";
		String sha;
		String owner = post.getUser().getNickname();
		githubApiClient.createRepositoryIfNotExists(token, repoName, owner);

		String tag = (tags != null && !tags.isEmpty()) ? tags.get(0) : "etc";
		String safeTitle = post.getTitle().replaceAll("[^\\w가-힣ㄱ-ㅎㅏ-ㅣ]+", "_");
		String fileName = post.getId() + "-" + safeTitle + ".md";
		String path = "PotatoStudy/" + tag + "/" + fileName;
		String msg = (commitMessage != null && !commitMessage.isBlank())
			? commitMessage
			: action + ": [" + tag + "] " + post.getTitle();

		if (oldTitle != null && oldTags != null) {
			String oldTag = (!oldTags.isEmpty()) ? oldTags.get(0) : "etc";
			String oldSafeTitle = oldTitle.replaceAll("[^\\w가-힣ㄱ-ㅎㅏ-ㅣ]+", "_");
			String oldFileName = post.getId() + "-" + oldSafeTitle + ".md";
			String oldPath = "PotatoStudy/" + oldTag + "/" + oldFileName;

			if (!oldPath.equals(path)) {
				githubApiClient.deleteFile(token, owner, repoName, oldPath, "Delete(old): " + oldFileName);
			}
		}

		if ("Delete".equals(action)) {
			sha = githubApiClient.deleteFile(token, owner, repoName, path, msg);
		} else {
			String markdown = buildMarkdownWithFrontmatter(post, tags);
			sha = githubApiClient.createOrUpdateFile(token, owner, repoName, path, msg, markdown);
		}
		return sha;
	}

	public String stripAllImages(String markdown) {
		if (markdown == null)
			return "";
		return markdown.replaceAll("!\\[[^\\]]*\\]\\([^\\)]*\\)", "");
	}

	public String makeSnippet(String markdown, int length) {
		String noImages = stripAllImages(markdown).trim();
		if (noImages.length() <= length) {
			return noImages;
		}
		return noImages.substring(0, length) + "...";
	}

	private String buildMarkdownWithFrontmatter(Post post, List<String> tags) {
		return post.getContent();
	}
}
