package org.gamja.gamzatechblog.domain.tag.service.impl;

import java.util.List;

import org.gamja.gamzatechblog.domain.tag.service.TagService;
import org.gamja.gamzatechblog.domain.tag.service.port.TagRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
	private final TagRepository tagRepository;

	@Override
	@Cacheable(value = "allTags")
	public List<String> getAllTags() {
		return tagRepository.findAllTagNames();
	}
}
