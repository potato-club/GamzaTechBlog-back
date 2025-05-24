package org.gamja.gamzatechblog.domain.posttag.util;

import java.util.List;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.posttag.model.entity.PostTag;
import org.gamja.gamzatechblog.domain.posttag.repository.PostTagRepository;
import org.gamja.gamzatechblog.domain.tag.model.entity.Tag;
import org.gamja.gamzatechblog.domain.tag.repository.TagRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostTagUtil {
	private final TagRepository tagRepository;
	private final PostTagRepository postTagRepository;

	@Transactional
	public void syncPostTags(Post post, List<String> tagNames) {
		postTagRepository.deleteAllByPost(post);
		post.getPostTags().clear();

		if (tagNames == null || tagNames.isEmpty()) {
			return;
		}

		for (String tagName : tagNames) {
			Tag tag = tagRepository.findByTagName(tagName)
				.orElseGet(() -> tagRepository.saveAndFlush(
					Tag.builder().tagName(tagName).build()
				));

			PostTag postTag = PostTag.builder()
				.post(post)
				.tag(tag)
				.build();

			postTagRepository.save(postTag);
			post.getPostTags().add(postTag);
		}
	}

}
