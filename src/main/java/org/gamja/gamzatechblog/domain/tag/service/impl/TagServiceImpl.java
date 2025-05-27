package org.gamja.gamzatechblog.domain.tag.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.gamja.gamzatechblog.domain.tag.model.dto.TagResponse;
import org.gamja.gamzatechblog.domain.tag.repository.TagRepository;
import org.gamja.gamzatechblog.domain.tag.service.TagService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
	private final TagRepository tagRepository;

	/*
	메인 페이지 호출용 전체 태그 보냄
	 */
	@Override
	public List<TagResponse> getAllTags() {
		return tagRepository.findAllTagNames().stream().map(TagResponse::new).collect(Collectors.toList());
	}
}
