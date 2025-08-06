package org.gamja.gamzatechblog.domain.post.controller;

import java.util.List;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.domain.post.controller.docs.PostControllerSwagger;
import org.gamja.gamzatechblog.domain.post.model.dto.request.PostRequest;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostDetailResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostListResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostPopularResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostResponse;
import org.gamja.gamzatechblog.domain.post.service.PostService;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@ApiController("/api/v1/posts")
@RequiredArgsConstructor
@Validated
public class PostController implements PostControllerSwagger {

	private final PostService postService;

	@PostMapping
	@Override
	public ResponseDto<PostResponse> publishPost(@CurrentUser User currentUser,
		@RequestBody PostRequest request) {
		PostResponse dto = postService.publishPost(currentUser, request);
		return ResponseDto.of(HttpStatus.CREATED, "게시물 작성 성공", dto);
	}

	@PutMapping("/{id}")
	public ResponseDto<PostResponse> revisePost(@CurrentUser User currentUser, @PathVariable Long id,
		@RequestBody PostRequest request) {
		PostResponse dto = postService.revisePost(currentUser, id, request);
		return ResponseDto.of(HttpStatus.OK, "게시물 수정 성공", dto);
	}

	@DeleteMapping("/{id}")
	public ResponseDto<Void> removePost(@CurrentUser User currentUser, @PathVariable Long id) {
		postService.removePost(currentUser, id);
		return ResponseDto.of(HttpStatus.OK, "게시물 삭제 성공");
	}

	@GetMapping
	public ResponseDto<PagedResponse<PostListResponse>> getPosts(
		@PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable,
		@RequestParam(name = "tags", required = false) List<String> filterTags) {

		PagedResponse<PostListResponse> paged = postService.getPosts(pageable, filterTags);
		return ResponseDto.of(HttpStatus.OK, "게시물 목록 조회 성공", paged);
	}

	@GetMapping("/{postId}")
	public ResponseDto<PostDetailResponse> getPostDetail(@PathVariable Long postId) {
		PostDetailResponse detail = postService.getPostDetail(postId);
		return ResponseDto.of(HttpStatus.OK, "게시물 상세 조회 성공", detail);
	}

	@GetMapping("/me")
	public ResponseDto<PagedResponse<PostListResponse>> getMyPosts(
		@CurrentUser User currentUser,
		@ParameterObject
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		PagedResponse<PostListResponse> page = postService.getMyPosts(currentUser, pageable);
		return ResponseDto.of(HttpStatus.OK, "내가 쓴 게시물 조회 성공", page);
	}

	@GetMapping("/popular")
	public ResponseDto<List<PostPopularResponse>> getWeeklyPopularPosts() {
		List<PostPopularResponse> popularList = postService.getWeeklyPopularPosts();
		return ResponseDto.of(HttpStatus.OK, "주간 인기 게시물 조회 성공", popularList);
	}

	@GetMapping("/tags/{tagName}")
	public ResponseDto<PagedResponse<PostListResponse>> getPostsByTag(
		@PathVariable String tagName,
		@ParameterObject
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		PagedResponse<PostListResponse> page = postService.getPostsByTag(tagName, pageable);
		return ResponseDto.of(HttpStatus.OK, String.format("'%s' 태그 게시물 조회 성공", tagName), page);
	}

	@GetMapping("/search")
	public ResponseDto<PagedResponse<PostListResponse>> searchPosts(
		@PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable,
		@RequestParam("keyword") String keyword) {
		PagedResponse<PostListResponse> page = postService.searchPostsByTitle(pageable, keyword);
		return ResponseDto.of(HttpStatus.OK, "게시물 검색 조회 성공", page);
	}
}
