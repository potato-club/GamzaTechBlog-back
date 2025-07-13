package org.gamja.gamzatechblog.domain.tag.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.gamja.gamzatechblog.domain.tag.service.impl.TagServiceImpl;
import org.gamja.gamzatechblog.domain.tag.service.port.TagFakeRepository;
import org.gamja.gamzatechblog.support.tag.TagFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TagServiceImpl 메서드 단위 테스트")
public class TagServiceImplTest {
	private TagFakeRepository tagFakeRepository;
	private TagService tagService;

	@BeforeEach
	void setUp() {
		tagFakeRepository = new TagFakeRepository();
		tagService = new TagServiceImpl(tagFakeRepository);
	}

	@Test
	@DisplayName("태그가 존재하면 모두 반환")
	void getAllTags_whenTagsExist_returnsList() {
		tagFakeRepository.save(TagFixtures.SPRING);
		tagFakeRepository.save(TagFixtures.DOCKER);

		List<String> result = tagService.getAllTags();

		assertThat(result).containsExactly("spring", "docker");
	}

	@Test
	@DisplayName("태그가 존재하지 않으면 반환X")
	void getAllTags_whenNoTags_returnsEmptyList() {
		List<String> result = tagService.getAllTags();

		assertThat(result).isEmpty();
	}
}
