package org.gamja.gamzatechblog.domain.image.port;

import org.gamja.gamzatechblog.domain.postimage.model.entity.PostImage;

public class PostImageFakeRepository extends FakeBaseRepository<PostImage> {
	public PostImage save(PostImage entity) {
		return super.save(id ->
				PostImage.builder()
					.id(id)
					.post(entity.getPost())
					.postImageUrl(entity.getPostImageUrl())
					.build(),
			entity,
			entity.getId());
	}
}