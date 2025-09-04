package org.gamja.gamzatechblog.domain.introduction.infrastructure;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.introduction.model.entity.Introduction;
import org.gamja.gamzatechblog.domain.introduction.service.port.IntroductionRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class IntroductionRepositoryImpl implements IntroductionRepository {

	private final IntroductionJpaRepository introductionJpaRepository;

	@Override
	public boolean existsByUser(User user) {
		return introductionJpaRepository.existsByUser(user);
	}

	@Override
	public Optional<Introduction> findByUser(User user) {
		return introductionJpaRepository.findByUser(user);
	}

	@Override
	public Optional<Introduction> findById(Long introId) {
		return introductionJpaRepository.findById(introId);
	}

	@Override
	public Introduction save(Introduction intro) {
		return introductionJpaRepository.save(intro);
	}

	@Override
	public void delete(Introduction intro) {
		introductionJpaRepository.delete(intro);
	}

	@Override
	public Page<Introduction> findAll(Pageable pageable) {
		return introductionJpaRepository.findAll(pageable);
	}
}