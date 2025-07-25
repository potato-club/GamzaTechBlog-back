package org.gamja.gamzatechblog.domain.like.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponse {
	private Long likeId;
	private Long postId;
	private String title;
	private String contentSnippet;
	private String writer;
	private String writerProfileImageUrl;
	private String thumbnailImageUrl;
	private LocalDateTime createdAt;
	private List<String> tags;
}
