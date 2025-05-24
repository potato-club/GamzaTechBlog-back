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

	public void syncToGitHub(String token, String owner, Post post, List<String> tags, String action) {
		String repoName = "GamjaTechBlog";
		githubApiClient.createRepositoryIfNotExists(token, repoName);

		String tag = (tags != null && !tags.isEmpty()) ? tags.get(0) : "etc";
		String safeTitle = post.getTitle().replaceAll("[^\\w가-힣]", "_");
		String fileName = post.getId() + "-" + safeTitle + ".md";
		String path = "PotatoStudy/" + tag + "/" + fileName;
		String msg = action + ": [" + tag + "] " + post.getTitle();

		if ("Delete".equals(action)) {
			githubApiClient.deleteFile(token, owner, repoName, path, msg);
		} else {
			githubApiClient.createOrUpdateFile(token, owner, repoName, path, msg, post.getContent());
		}
	}
}
