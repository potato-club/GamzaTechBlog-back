package org.gamja.gamzatechblog.domain.post.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.post.util.PostUtil;

public record PostListResponse(
	Long postId,
	String title,
	String contentSnippet,
	String writer,
	String writerProfileImageUrl,
	LocalDateTime createdAt,
	List<String> tags,
	String thumbnailImageUrl
) {
	public PostListResponse(Post p, PostUtil postUtil, Map<Long, String> firstImageByPost) {
		this(
			p.getId(),
			p.getTitle(),
			postUtil.makeSnippet(p.getContent(), 100),
			p.getUser().getNickname(),
			p.getUser().getProfileImage() != null
				? p.getUser().getProfileImage().getProfileImageUrl()
				: null,
			p.getCreatedAt(),
			p.getPostTags().stream()
				.map(pt -> pt.getTag().getTagName())
				.toList(),
			firstImageByPost.get(p.getId())
		);
	}
}
