package org.gamja.gamzatechblog.domain.postimage.infrastructure;

import java.util.List;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.postimage.model.entity.PostImage;
import org.gamja.gamzatechblog.domain.postimage.service.port.PostImageRepository;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostImageRepositoryImpl implements PostImageRepository {

	private final PostImageJpaRepository postImageJpaRepository;

	@Override
	public PostImage postImageSave(PostImage postImage) {
		return postImageJpaRepository.save(postImage);
	}

	@Override
	public List<PostImage> findAllByPostOrderById(Post post) {
		return postImageJpaRepository.findAllByPostOrderById(post);
	}

	@Override
	public void deleteAllByPost(Post post) {
		postImageJpaRepository.deleteAllByPost(post);
	}
}
