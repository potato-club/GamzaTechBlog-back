package org.gamja.gamzatechblog.domain.tag.service.impl;

import java.util.List;

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
	public List<String> getAllTags() {
		// findAllTagNames()이 이미 List<String>을 반환한다면
		return tagRepository.findAllTagNames();
	}
}
