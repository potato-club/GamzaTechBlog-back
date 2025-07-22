package org.gamja.gamzatechblog.domain.profileimage.controller;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.domain.profileimage.controller.response.ProfileImageResponse;
import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.profileimage.model.mapper.ProfileImageMapper;
import org.gamja.gamzatechblog.domain.profileimage.service.ProfileImageService;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@ApiController("/api/v1/profile-images")
@Tag(name = "프로필 이미지 기능", description = "프로필 사진 업로드·조회·수정·삭제 API")
@RequiredArgsConstructor
public class ProfileImageController {

	private final ProfileImageService profileImageService;
	private final ProfileImageMapper profileImageMapper;

	@Operation(summary = "프로필 사진 업로드")
	@PostMapping(
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseDto<ProfileImageResponse> uploadProfileImage(
		@RequestPart("file") MultipartFile file,
		@CurrentUser User user
	) {
		ProfileImage saved = profileImageService.uploadProfileImage(file, user);
		ProfileImageResponse body = profileImageMapper.toProfileImageResponse(saved);
		return ResponseDto.of(HttpStatus.CREATED, "프로필 이미지 업로드 성공", body);
	}

	@Operation(summary = "프로필 사진 확인")
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseDto<ProfileImageResponse> getProfileImage(
		@CurrentUser User user
	) {
		ProfileImage found = profileImageService.getProfileImageByUser(user);
		ProfileImageResponse body = profileImageMapper.toProfileImageResponse(found);
		return ResponseDto.of(HttpStatus.OK, "프로필 이미지 조회 성공", body);
	}

	@Operation(summary = "프로필 사진 수정")
	@PutMapping(
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseDto<ProfileImageResponse> updateProfileImage(
		@RequestPart("file") MultipartFile newFile,
		@CurrentUser User user
	) {
		ProfileImage existing = profileImageService.getProfileImageByUser(user);
		ProfileImage updated = profileImageService.updateProfileImage(newFile, existing);
		ProfileImageResponse body = profileImageMapper.toProfileImageResponse(updated);
		return ResponseDto.of(HttpStatus.OK, "프로필 이미지 수정 성공", body);
	}

	@Operation(summary = "프로필 사진 삭제")
	@DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseDto<Void> deleteProfileImage(
		@CurrentUser User user
	) {
		ProfileImage existing = profileImageService.getProfileImageByUser(user);
		profileImageService.deleteProfileImage(existing);
		return ResponseDto.of(HttpStatus.OK, "프로필 이미지 삭제 성공");
	}
}
