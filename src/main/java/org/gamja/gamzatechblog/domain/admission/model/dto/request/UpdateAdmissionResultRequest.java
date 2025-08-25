package org.gamja.gamzatechblog.domain.admission.model.dto.request;

import org.gamja.gamzatechblog.domain.admission.model.type.AdmissionStatus;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateAdmissionResultRequest(
	@Size(max = 100, message = "name은 최대 100자까지 허용됩니다")
	String name,

	@Size(max = 20, message = "phone은 정규화 전 기준으로 최대 20자까지 허용")
	@Pattern(regexp = "^[0-9\\s+\\-()]*$", message = "phone은 숫자, 공백, +, -, 괄호만 허용됩니다")
	String phone,

	AdmissionStatus status
) {
}
