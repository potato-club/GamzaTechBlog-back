package org.gamja.gamzatechblog.domain.admission.controller;

import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.domain.admission.model.dto.CreateAdmissionResultRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.LookupRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.LookupResponse;
import org.gamja.gamzatechblog.domain.admission.service.AdmissionService;
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
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@ApiController("/api/admissions")
@RequiredArgsConstructor
@Validated
public class AdmissionController {
	private final AdmissionService admissionService;

	@Operation(summary = "합격/불합격 조회 (공개 GET)", tags = "합격/불합격")
	@GetMapping("/lookup")
	public ResponseDto<LookupResponse> lookup(@Valid LookupRequest request) {
		LookupResponse result = admissionService.getAdmissionStatusByNameAndPhone(request);
		return ResponseDto.of(HttpStatus.OK, "합격/불합격 조회 성공", result);
	}

	@Operation(summary = "합격/불합격 결과 생성 (ADMIN)", tags = "관리자 기능")
	@PostMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseDto<Long> create(@RequestBody @Valid CreateAdmissionResultRequest request) {
		Long id = admissionService.createAdmissionResult(request);
		return ResponseDto.of(HttpStatus.CREATED, "합격/불합격 결과 생성 완료", id);
	}

	@Operation(summary = "합격/불합격 결과 삭제 (ADMIN)", tags = "관리자 기능")
	@DeleteMapping("/admin/{admissionId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseDto<Void> delete(@PathVariable("admissionId") @Positive Long admissionId) {
		admissionService.deleteAdmissionResultById(admissionId);
		return ResponseDto.of(HttpStatus.NO_CONTENT, "합격/불합격 결과 삭제 완료", null);
	}
}
