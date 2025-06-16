package org.gamja.gamzatechblog.domain.like.controller;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.domain.like.dto.response.LikeResponse;
import org.gamja.gamzatechblog.domain.like.service.LikeService;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApiController("/api/v1/likes")
public class LikeController {

	private final LikeService likeService;

	//나중에 쿼리dsl로 리팩토링 예정
	@Operation(summary = "내가 누른 좋아요 목록", tags = "좋아요 기능")
	@GetMapping("/me")
	public ResponseEntity<ResponseDto<PagedResponse<LikeResponse>>> getMyLikes(
		@CurrentUser User currentUser,
		@ParameterObject
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		PagedResponse<LikeResponse> page = likeService.getMyLikes(currentUser, pageable);
		return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, "좋아요 목록 조회 성공", page));
	}

	@Operation(summary = "게시글 좋아요", tags = "좋아요 기능")
	@PostMapping("/{postId}")
	public ResponseEntity<ResponseDto<LikeResponse>> likePost(@CurrentUser User currentUser,
		@PathVariable Long postId) {
		LikeResponse response = likeService.likePost(currentUser, postId);
		return ResponseEntity.ok(ResponseDto.of(HttpStatus.CREATED, "좋아요 추가 성공", response));
	}

	@Operation(summary = "좋아요 취소", tags = "좋아요 기능")
	@DeleteMapping("/{postId}")
	public ResponseEntity<ResponseDto<Void>> unlikePost(@CurrentUser User currentUser, @PathVariable Long postId) {
		likeService.unlikePost(currentUser, postId);
		return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, "좋아요 취소 성공", null));
	}
}

