package org.gamja.gamzatechblog.domain.post.model.dto.request;

import java.util.List;

import lombok.Getter;

@Getter
public class PostRequest {
	private String title;
	private String content;
	private List<String> tags;

	public void setTitle(String title) {
		this.title = title;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
}

