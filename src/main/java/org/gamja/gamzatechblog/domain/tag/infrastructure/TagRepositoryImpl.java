package org.gamja.gamzatechblog.domain.tag.infrastructure;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.tag.model.entity.Tag;
import org.gamja.gamzatechblog.domain.tag.service.port.TagRepository;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TagRepositoryImpl implements TagRepository {

	private final TagJpaRepository tagJpaRepository;

	@Override
	public Optional<Tag> findByTagName(String tagName) {
		return tagJpaRepository.findByTagName(tagName);
	}

	@Override
	public List<String> findAllTagNames() {
		return tagJpaRepository.findAllTagNames();
	}

	@Override
	public Tag save(Tag tag) {
		return tagJpaRepository.save(tag);
	}

	@Override
	public void delete(Tag tag) {
		tagJpaRepository.delete(tag);
	}

	@Override
	public Tag saveAndFlush(Tag tag) {
		return tagJpaRepository.saveAndFlush(tag);
	}

}
