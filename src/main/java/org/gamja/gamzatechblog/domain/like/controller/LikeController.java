package org.gamja.gamzatechblog.domain.like.controller;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.domain.like.controller.docs.LikeControllerSwagger;
import org.gamja.gamzatechblog.domain.like.model.dto.response.LikeResponse;
import org.gamja.gamzatechblog.domain.like.service.LikeService;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApiController("/api/v1/likes")
public class LikeController implements LikeControllerSwagger {

	private final LikeService likeService;

	@GetMapping("/me")
	public ResponseDto<PagedResponse<LikeResponse>> getMyLikes(@CurrentUser User currentUser, @ParameterObject
	@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseDto.of(HttpStatus.OK, "좋아요 목록 조회 성공", likeService.getMyLikes(currentUser, pageable));
	}

	@PostMapping("/{postId}")
	public ResponseDto<String> likePost(@CurrentUser User currentUser, @PathVariable Long postId) {
		likeService.likePost(currentUser, postId);
		return ResponseDto.of(HttpStatus.CREATED, "좋아요 추가 성공");
	}

	@DeleteMapping("/{postId}")
	public ResponseDto<String> unlikePost(@CurrentUser User currentUser, @PathVariable Long postId) {
		likeService.unlikePost(currentUser, postId);
		return ResponseDto.of(HttpStatus.OK, "좋아요 취소 성공");
	}

	@GetMapping("/{postId}/liked")
	public ResponseDto<Boolean> isPostLiked(@CurrentUser User currentUser, @PathVariable Long postId) {
		boolean liked = likeService.isPostLiked(currentUser, postId);
		return ResponseDto.of(HttpStatus.OK, "좋아요 여부 조회 성공", liked);
	}
}

