package org.gamja.gamzatechblog.domain.commithistory.controller;

import java.util.List;

import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.domain.commithistory.model.dto.response.CommitHistoryResponse;
import org.gamja.gamzatechblog.domain.commithistory.service.CommitHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@ApiController("/api/posts/commits")
@RequiredArgsConstructor
public class CommitHistoryController {
	private final CommitHistoryService commitHistoryService;

	@Operation(summary = "게시물 커밋 내역", tags = "커밋 조회 기능")
	@GetMapping("/{postId}")
	public ResponseEntity<ResponseDto<List<CommitHistoryResponse>>> getCommitHistories(@PathVariable Long postId) {
		List<CommitHistoryResponse> list = commitHistoryService.getCommitHistoryListByPostId(postId);
		return ResponseEntity.ok(ResponseDto.of(org.springframework.http.HttpStatus.OK, "커밋 이력 조회 성공", list));
	}
}