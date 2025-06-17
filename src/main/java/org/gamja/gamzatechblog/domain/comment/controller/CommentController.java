package org.gamja.gamzatechblog.domain.comment.controller;

import java.util.List;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.domain.comment.model.dto.request.CommentRequest;
import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentListResponse;
import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentResponse;
import org.gamja.gamzatechblog.domain.comment.service.CommentService;
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
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@ApiController("/api/v1/comment")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@Operation(summary = "게시물의 댓글 목록 조회", tags = {"댓글 기능"})
	@GetMapping("/{postId}/comments")
	public ResponseDto<List<CommentResponse>> getComments(@PathVariable Long postId) {
		List<CommentResponse> commentResponses = commentService.getCommentsByPostId(postId);
		return ResponseDto.of(HttpStatus.OK, "댓글 목록 조회 성공", commentResponses);
	}

	@Operation(summary = "게시물에 댓글 등록", tags = {"댓글 기능"})
	@PostMapping("/{postId}/comments")
	public ResponseDto<CommentResponse> addComment(@CurrentUser User currentUser,
		@PathVariable Long postId, @RequestBody CommentRequest commentRequest) {
		CommentResponse createdComment = commentService.createComment(currentUser, postId, commentRequest);
		return ResponseDto.of(HttpStatus.CREATED, "댓글이 등록되었습니다.", createdComment);
	}

	@Operation(summary = "댓글 삭제", tags = {"댓글 기능"})
	@DeleteMapping("/{commentId}")
	public ResponseDto<String> deleteComment(@CurrentUser User currentUser,
		@PathVariable Long commentId) {
		commentService.deleteComment(currentUser, commentId);
		return ResponseDto.of(HttpStatus.OK, "댓글이 삭제되었습니다.");
	}

	//코드 수정예정
	@Operation(summary = "내가 단 댓글 목록 조회", tags = "댓글 기능")
	@GetMapping("/me/comments")
	public ResponseDto<PagedResponse<CommentListResponse>> getMyComments(
		@CurrentUser User currentUser,
		@ParameterObject
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		PagedResponse<CommentListResponse> page = commentService.getMyComments(currentUser, pageable);
		return ResponseDto.of(HttpStatus.OK, "내가 단 댓글 목록 조회 성공", page);
	}
}
