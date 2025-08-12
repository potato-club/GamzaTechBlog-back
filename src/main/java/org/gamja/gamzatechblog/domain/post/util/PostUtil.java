package org.gamja.gamzatechblog.domain.post.util;

import java.util.List;

import org.gamja.gamzatechblog.core.auth.oauth.client.GithubApiClient;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.post.util.github.GitAction;
import org.gamja.gamzatechblog.domain.post.util.github.GitDeleteCmd;
import org.gamja.gamzatechblog.domain.post.util.github.GitSyncCmd;
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

	public String syncToGitHub(GitSyncCmd cmd) {
		Post post = cmd.post();
		String owner = cmd.owner();
		githubApiClient.createRepositoryIfNotExists(cmd.token(), REPO_NAME, owner);

		String path = buildPostPath(post.getId(), post.getTitle(), cmd.tags());
		String msg = messageOrDefault(cmd.commitMessage(), cmd.action().value, primaryTag(cmd.tags()), post.getTitle());

		if (cmd.oldTitle() != null && cmd.oldTags() != null) {
			String oldPath = buildPostPath(post.getId(), cmd.oldTitle(), cmd.oldTags());
			if (!oldPath.equals(path)) {
				String oldFileName = fileName(post.getId(), sanitize(cmd.oldTitle()));
				githubApiClient.deleteFile(cmd.token(), owner, REPO_NAME, oldPath, "Delete(old): " + oldFileName);
			}
		}

		if (cmd.action() == GitAction.DELETE) {
			return githubApiClient.deleteFile(cmd.token(), owner, REPO_NAME, path, msg);
		} else {
			String markdown = buildMarkdownWithFrontmatter(post, cmd.tags());
			return githubApiClient.createOrUpdateFile(cmd.token(), owner, REPO_NAME, path, msg, markdown);
		}
	}

	public String deleteFromGitHub(GitDeleteCmd cmd) {
		String path = buildPostPath(cmd.postId(), cmd.prevTitle(), cmd.prevTags());
		String msg = messageOrDefault(cmd.commitMessage(), GitAction.DELETE.value, primaryTag(cmd.prevTags()),
			cmd.prevTitle());
		return githubApiClient.deleteFile(cmd.token(), cmd.owner(), REPO_NAME, path, msg);
	}

	private String buildPostPath(long postId, String title, List<String> tags) {
		String tag = sanitize(primaryTag(tags));
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
