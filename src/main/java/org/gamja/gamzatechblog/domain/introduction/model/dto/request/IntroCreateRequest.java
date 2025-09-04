package org.gamja.gamzatechblog.domain.introduction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IntroCreateRequest(
	@NotBlank(message = "자기소개 내용은 필수입니다.")
	@Size(min = 2, max = 1000, message = "자기소개는 2~1000자 사이로 입력하세요.")
	String content
) {
}
