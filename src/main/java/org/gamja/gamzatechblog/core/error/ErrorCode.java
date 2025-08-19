package org.gamja.gamzatechblog.core.error;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

	// Common
	INVALID_INPUT_VALUE(400, "C001", "잘못된 입력 값입니다."),
	METHOD_NOT_ALLOWED(405, "C002", "허용되지 않은 메서드입니다."),
	ENTITY_NOT_FOUND(400, "C003", "엔티티를 찾을 수 없습니다."),
	INTERNAL_SERVER_ERROR(500, "C004", "서버 내부 오류가 발생했습니다."),
	INVALID_TYPE_VALUE(400, "C005", "유효하지 않은 타입 값입니다."),
	HANDLE_ACCESS_DENIED(403, "C006", "접근이 거부되었습니다."),

	// JWT
	EXPIRED_JWT(403, "J001", "만료된 JWT 토큰입니다."),
	UNSUPPORTED_JWT(403, "J002", "지원되지 않는 JWT 토큰입니다."),
	SIGNATURE_INVALID_JWT(403, "JOO3", "사용중인 시그니처키입니다."),
	JWT_NOT_FOUND(403, "J004", "JWT 토큰을 찾을 수 없습니다."),
	AUTHENTICATION_FAILED(403, "J005", "인증에 실패했습니다."),

	// User
	USER_NOT_FOUND(404, "U001", "사용자를 찾을 수 없습니다."),
	GITHUB_USER_NOT_FOUND(404, "U002", "깃허브 사용자를 찾을 수 없습니다."),
	USER_ALREADY_REGISTERED(409, "U003", "이미 가입된 사용자입니다."),
	EMAIL_ALREADY_EXISTS(409, "U004", "이미 사용 중인 이메일입니다."),
	NICKNAME_ALREADY_EXISTS(409, "U005", "이미 사용 중인 닉네임입니다."),

	// Profile
	PROFILE_INCOMPLETE(400, "U006", "프로필 정보가 완전하지 않습니다."),
	POSITION_REQUIRED(400, "U007", "직군 정보는 필수입니다."),

	// GitHub OAuth Token
	GITHUB_OAUTH_TOKEN_NOT_FOUND(404, "G001", "깃허브 OAuth 토큰을 찾을 수 없습니다."),
	GITHUB_OAUTH_TOKEN_INVALID(401, "G002", "유효하지 않은 깃허브 OAuth 토큰입니다."),

	// Post
	POST_NOT_FOUND(404, "P001", "게시물을 찾을 수 없습니다."),

	// Comment
	COMMENT_NOT_FOUND(404, "CM001", "댓글을 찾을 수 없습니다."),
	COMMENT_FORBIDDEN(403, "CM002", "댓글 작성자만 수정/삭제할 수 있습니다."),
	PARENT_COMMENT_NOT_FOUND(404, "CM003", "상위 댓글을 찾을 수 없습니다."),
	COMMENT_INVALID_PARENT(400, "CM004", "상위 댓글이 게시글에 속하지 않습니다."),
	COMMENT_UNSUPPORTED_ACTION(400, "CM005", "지원하지 않는 댓글 작업입니다."),

	// 500 Internal Server Error: 서비스/서버 오류
	OAUTH_PROVIDER_ERROR(500, "S5001", "OAuth 프로바이더 오류입니다."),

	// Like
	ALREADY_LIKED(409, "L001", "이미 좋아요를 눌렀습니다."),
	LIKE_NOT_FOUND(404, "L002", "좋아요를 찾을 수 없습니다."),

	// Profile Image
	PROFILE_IMAGE_EMPTY(400, "U008", "업로드할 파일이 없습니다."),
	PROFILE_IMAGE_SIZE_EXCEEDED(400, "U009", "파일 크기가 최대 용량을 초과했습니다."),
	PROFILE_IMAGE_INVALID_TYPE(400, "U010", "지원하지 않는 파일 형식입니다."),
	PROFILE_IMAGE_UPLOAD_FAILED(500, "U011", "프로필 이미지 업로드에 실패했습니다."),
	PROFILE_IMAGE_DELETE_FAILED(500, "U012", "프로필 이미지 삭제에 실패했습니다."),

	// Project
	PROJECT_NOT_FOUND(404, "PR001", "프로젝트를 찾을 수 없습니다."),
	PROJECT_IMAGE_EMPTY(400, "PR002", "업로드할 프로젝트 이미지가 없습니다."),
	PROJECT_IMAGE_INVALID_TYPE(400, "PR003", "지원하지 않는 프로젝트 이미지 형식입니다."),
	PROJECT_NOT_OWNER(403, "PR004", "프로젝트 소유자가 아닙니다."),

	// ADMIN
	ADMIN_ACCESS_FORBIDDEN(403, "A001", "어드민 권한이 필요합니다."),

	// Admission
	ADMISSION_RESULT_DUPLICATED(409, "AD001", "이미 등록된 합격/불합격 결과입니다."),
	ADMISSION_RESULT_NOT_FOUND(404, "AD002", "합격/불합격 결과를 찾을 수 없습니다."),
	ADMISSION_INVALID_PHONE(400, "AD003", "유효하지 않은 전화번호 형식입니다."),
	ADMISSION_INVALID_STATUS(400, "AD004", "유효하지 않은 상태 값입니다.");

	private final String code;
	private final String message;
	private int status;

	ErrorCode(final int status, final String code, final String message) {
		this.status = status;
		this.message = message;
		this.code = code;
	}

	public String getMessage() {
		return this.message;
	}

	public String getCode() {
		return code;
	}

	public int getStatus() {
		return status;
	}
}