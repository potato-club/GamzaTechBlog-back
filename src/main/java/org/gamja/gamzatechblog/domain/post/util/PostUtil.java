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

	public void syncToGitHub(String token, String oldTitle, List<String> oldTags,
		Post post, List<String> tags, String action) {
		String repoName = "GamzaTechBlog";
		String owner = post.getUser().getNickname();
		githubApiClient.createRepositoryIfNotExists(token, repoName);

		String tag = (tags != null && !tags.isEmpty()) ? tags.get(0) : "etc";
		String safeTitle = post.getTitle().replaceAll("[^\\w가-힣]", "_");
		String fileName = post.getId() + "-" + safeTitle + ".md";
		String path = "PotatoStudy/" + tag + "/" + fileName;
		String msg = action + ": [" + tag + "] " + post.getTitle();

		if ("Update".equals(action) && oldTitle != null && oldTags != null) {
			String oldTag = (!oldTags.isEmpty()) ? oldTags.get(0) : "etc";
			String oldSafeTitle = oldTitle.replaceAll("[^\\w가-힣]", "_");
			String oldFileName = post.getId() + "-" + oldSafeTitle + ".md";
			String oldPath = "PotatoStudy/" + oldTag + "/" + oldFileName;

			if (!oldPath.equals(path)) {
				githubApiClient.deleteFile(token, owner, repoName, oldPath, "Delete(old): " + oldFileName);
			}
		}

		if ("Delete".equals(action)) {
			githubApiClient.deleteFile(token, owner, repoName, path, msg);
		} else {
			String markdown = buildMarkdownWithFrontmatter(post, tags);
			githubApiClient.createOrUpdateFile(token, owner, repoName, path, msg, markdown);
		}
	}

	private String buildMarkdownWithFrontmatter(Post post, List<String> tags) {
		return post.getContent();
	}
}
