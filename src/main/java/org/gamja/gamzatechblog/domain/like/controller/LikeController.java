package org.gamja.gamzatechblog.domain.like.controller;

import java.util.List;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.domain.like.dto.response.LikeResponse;
import org.gamja.gamzatechblog.domain.like.service.LikeService;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
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

	@Operation(summary = "내가 좋아요한 글 목록 (최신순)", tags = "좋아요 기능")
	@GetMapping("/me")
	public ResponseEntity<ResponseDto<List<LikeResponse>>> getMyLikes(@CurrentUser User currentUser) {
		List<LikeResponse> likes = likeService.getMyLikes(currentUser);
		return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, "내 좋아요 조회 성공", likes));
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

