package org.gamja.gamzatechblog.domain.post.controller;

import java.util.List;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.domain.post.model.dto.request.PostRequest;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostDetailResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostListResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostResponse;
import org.gamja.gamzatechblog.domain.post.service.PostService;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@Operation(summary = "게시물 작성", tags = "게시물 기능")
	@PostMapping
	public ResponseEntity<ResponseDto<PostResponse>> publishPost(@CurrentUser User currentUser,
		@RequestBody PostRequest request) {
		PostResponse dto = postService.publishPost(currentUser, request);
		return ResponseEntity.ok(ResponseDto.of(HttpStatus.CREATED, "게시물 작성 성공", dto));
	}

	@Operation(summary = "게시물 수정", tags = "게시물 기능")
	@PutMapping("/{id}")
	public ResponseEntity<ResponseDto<PostResponse>> revisePost(@CurrentUser User currentUser, @PathVariable Long id,
		@RequestBody PostRequest request) {
		PostResponse dto = postService.revisePost(currentUser, id, request);
		return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, "게시물 수정 성공", dto));
	}

	@Operation(summary = "게시물 삭제", tags = "게시물 기능")
	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseDto<Void>> removePost(@CurrentUser User currentUser, @PathVariable Long id) {
		postService.removePost(currentUser, id);
		return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, "게시물 삭제 성공", null));
	}

	@Operation(summary = "최신순 게시물 목록 조회", tags = "게시물 조회 기능")
	@GetMapping
	public ResponseEntity<ResponseDto<PagedResponse<PostListResponse>>> getPosts(
		@PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable,
		@RequestParam(name = "tags", required = false) List<String> filterTags) {
		PagedResponse<PostListResponse> paged = postService.getPosts(pageable, filterTags);
		return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, "게시물 목록 조회 성공", paged));
	}

	@Operation(summary = "단일 게시물 상세 조회", tags = "게시물 조회 기능")
	@GetMapping("/{postId}")
	public ResponseEntity<ResponseDto<PostDetailResponse>> getPostDetail(@PathVariable Long postId) {
		PostDetailResponse detail = postService.getPostDetail(postId);
		return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, "게시물 상세 조회 성공", detail));
	}
}
