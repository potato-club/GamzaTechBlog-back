package org.gamja.gamzatechblog.domain.post.controller;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.domain.post.model.dto.request.PostRequest;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostResponse;
import org.gamja.gamzatechblog.domain.post.service.PostService;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
