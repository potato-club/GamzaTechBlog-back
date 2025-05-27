package org.gamja.gamzatechblog.domain.post.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostListResponse {
	private Long postId;
	private String title;
	private String contentSnippet;
	private String writer;
	private LocalDateTime createdAt;
	private List<String> tags;
}
