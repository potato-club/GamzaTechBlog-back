package org.gamja.gamzatechblog.domain.tag.service;

import java.util.List;

import org.gamja.gamzatechblog.domain.tag.model.dto.TagResponse;

public interface TagService {
	List<TagResponse> getAllTags();
}
