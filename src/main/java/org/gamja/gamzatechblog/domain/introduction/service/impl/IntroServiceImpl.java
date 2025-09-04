package org.gamja.gamzatechblog.domain.introduction;

import java.util.List;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
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

	@Override
	@Transactional
	public IntroResponse create(User user, String content) {
		if (introductionRepository.existsByUser(user)) {
			throw new IllegalStateException("이미 자기소개가 존재합니다.");
		}
		Introduction saved = introductionRepository.save(
			Introduction.builder()
				.user(user)
				.content(content)
				.build()
		);
		return introMapper.toResponse(saved);
	}

	@Override
	public PagedResponse<IntroResponse> list(Pageable pageable) {
		Page<Introduction> page = introductionRepository.findAll(pageable);
		return PagedResponse.of(page, introMapper::toResponse);
	}

	@Override
	public List<IntroResponse> getIntros() {
		return introMapper.toResponseList(introductionRepository.findAllOrderByCreatedDesc());
	}

	@Override
	public IntroResponse getMine(User user) {
		Introduction intro = introductionRepository.findByUser(user)
			.orElseThrow(() -> new IllegalArgumentException("자기소개가 없습니다."));
		return introMapper.toResponse(intro);
	}

	@Override
	public IntroResponse getByUserId(Long userId) {
		Introduction intro = introductionRepository.findByUserId(userId)
			.orElseThrow(() -> new IllegalArgumentException("자기소개가 없습니다."));
		return introMapper.toResponse(intro);
	}

	@Override
	@Transactional
	public void delete(Long introId, User user) {
		Introduction intro = introductionRepository.findById(introId)
			.orElseThrow(() -> new IllegalArgumentException("자기소개가 없습니다."));
		if (!intro.getUser().getId().equals(user.getId())) {
			throw new IllegalStateException("본인만 자기소개를 삭제할 수 있습니다.");
		}
		introductionRepository.delete(intro);
	}
}
