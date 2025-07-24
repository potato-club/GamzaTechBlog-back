package org.gamja.gamzatechblog.domain.post.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostPopularResponse {
	private String title;
	private String writer;
	private String writerProfileImageUrl;
}
