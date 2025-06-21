package org.gamja.gamzatechblog.domain.post.service.port;

import java.util.List;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostQueryPort {
	/**
 * Retrieves a paginated list of posts, optionally filtered by the specified tag names.
 *
 * @param pageable pagination and sorting information
 * @param tagNames list of tag names to filter posts by; if empty or null, no tag filtering is applied
 * @return a page of posts matching the filter and pagination criteria
 */
Page<Post> findAllPosts(Pageable pageable, List<String> tagNames);
}
