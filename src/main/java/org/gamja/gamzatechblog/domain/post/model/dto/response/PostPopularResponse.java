package org.gamja.gamzatechblog.domain.post.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostPopularResponse {
	private Long postId;
	private String title;
	private String writer;
	private String writerProfileImageUrl;
}
