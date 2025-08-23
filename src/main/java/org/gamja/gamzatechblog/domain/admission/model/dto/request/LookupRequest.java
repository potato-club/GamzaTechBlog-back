package org.gamja.gamzatechblog.domain.admission.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LookupRequest(
	@NotBlank String name,
	@NotBlank
	@Pattern(regexp = "^01[016789]\\d{7,8}$")
	String phone
) {
}
