package org.gamja.gamzatechblog.domain.introduction.service.impl;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.introduction.model.dto.response.IntroResponse;
import org.gamja.gamzatechblog.domain.introduction.model.entity.Introduction;
import org.gamja.gamzatechblog.domain.introduction.model.mapper.IntroMapper;
import org.gamja.gamzatechblog.domain.introduction.service.IntroService;
import org.gamja.gamzatechblog.domain.introduction.service.port.IntroductionRepository;
import org.gamja.gamzatechblog.domain.introduction.validator.IntroValidator;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IntroServiceImpl implements IntroService {

	private final IntroductionRepository introductionRepository;
	private final IntroMapper introMapper;
	private final IntroValidator introValidator;

	@Override
	@Transactional
	@CacheEvict(value = "introsList", allEntries = true)
	public IntroResponse create(User user, String content) {
		introValidator.validateNotExists(user);
		Introduction saved = introductionRepository.save(introMapper.newIntroduction(user, content));
		return introMapper.toResponse(saved);
	}

	@Override
	@Cacheable(
		value = "introsList",
		key = "'p:' + #pageable.pageNumber + ':s:' + #pageable.pageSize + ':sort:' + #pageable.sort"
	)
	public PagedResponse<IntroResponse> list(Pageable pageable) {
		Page<Introduction> page = introductionRepository.findAll(pageable);
		return PagedResponse.of(page, introMapper::toResponse);
	}

	@Override
	@Transactional
	@CacheEvict(value = "introsList", allEntries = true)
	public void delete(Long introId, User user) {
		Introduction intro = introValidator.validateExists(introId);
		introValidator.validateOwner(intro, user);
		introductionRepository.delete(intro);
	}
}
