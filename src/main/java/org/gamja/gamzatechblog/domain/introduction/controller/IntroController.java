package org.gamja.gamzatechblog.domain.introduction;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@ApiController("/api/v1/intros")
@RequiredArgsConstructor
@Validated
public class IntroController {

	private final IntroService introService;

	@Operation(summary = "자기소개 작성", tags = "텃밭인사")
	@PostMapping
	@PreAuthorize("hasAnyRole('USER','PRE_REGISTER','ADMIN')")
	public ResponseDto<IntroResponse> create(
		@CurrentUser User currentUser,
		@Valid @RequestBody IntroCreateRequest request
	) {
		IntroResponse dto = introService.create(currentUser, request.content());
		return ResponseDto.of(HttpStatus.CREATED, "자기소개 작성 성공", dto);
	}

	@Operation(summary = "자기소개 목록 조회(페이지네이션)", tags = "텃밭인사")
	@GetMapping
	public ResponseDto<PagedResponse<IntroResponse>> list(
		@ParameterObject
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return ResponseDto.of(HttpStatus.OK, "자기소개 목록 조회 성공",
			introService.list(pageable));
	}

	@Operation(summary = "내 자기소개 조회", tags = "텃밭인사")
	@GetMapping("/me")
	public ResponseDto<IntroResponse> getMine(@CurrentUser User currentUser) {
		return ResponseDto.of(HttpStatus.OK, "내 자기소개 조회 성공",
			introService.getMine(currentUser));
	}

	@Operation(summary = "특정 유저의 자기소개 조회", tags = "텃밭인사")
	@GetMapping("/users/{userId}")
	public ResponseDto<IntroResponse> getByUser(@PathVariable Long userId) {
		return ResponseDto.of(HttpStatus.OK, "자기소개 조회 성공",
			introService.getByUserId(userId));
	}

	@Operation(summary = "자기소개 삭제(본인만)", tags = "텃밭인사")
	@DeleteMapping("/{introId}")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseDto<Void> delete(
		@PathVariable Long introId,
		@CurrentUser User currentUser
	) {
		introService.delete(introId, currentUser);
		return ResponseDto.of(HttpStatus.OK, "자기소개 삭제 성공");
	}
}
