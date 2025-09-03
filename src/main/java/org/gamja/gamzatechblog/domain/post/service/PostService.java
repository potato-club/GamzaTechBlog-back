package org.gamja.gamzatechblog.domain.post.service;

import java.util.List;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.request.PostRequest;
import org.gamja.gamzatechblog.domain.post.model.dto.response.HomeFeedResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostDetailResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostListResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostPopularResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Pageable;

public interface PostService {
	PostResponse publishPost(User currentUser, PostRequest request);

	PostResponse revisePost(User currentUser, Long postId, PostRequest request);

	void removePost(User currentUser, Long postId);

	PagedResponse<PostListResponse> getPosts(Pageable pageable, List<String> filterTags);

	PostDetailResponse getPostDetail(Long postId);

	PagedResponse<PostListResponse> getMyPosts(User currentUser, Pageable pageable);

	List<PostPopularResponse> getWeeklyPopularPosts();

	PagedResponse<PostListResponse> getPostsByTag(String tagName, Pageable pageable);

	PagedResponse<PostListResponse> searchPostsByTitle(Pageable pageable, String keyword);

	HomeFeedResponse getHomeFeed(Pageable pageable, List<String> filterTags);
}
