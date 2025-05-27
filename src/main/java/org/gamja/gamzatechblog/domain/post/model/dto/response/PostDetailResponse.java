package org.gamja.gamzatechblog.domain.post.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponse {
	private Long postId;
	private String title;
	private String content;
	private String writer;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<String> tags;
	private List<CommentResponse> comments;
}


