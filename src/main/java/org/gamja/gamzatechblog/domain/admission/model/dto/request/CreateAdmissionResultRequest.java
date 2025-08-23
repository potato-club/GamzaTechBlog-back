package org.gamja.gamzatechblog.domain.admission.model.dto.request;

import org.gamja.gamzatechblog.domain.admission.model.type.AdmissionStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateAdmissionResultRequest(
	@NotBlank String name,
	@NotBlank @Pattern(regexp = "^01[016789]\\d{7,8}$") String phone,
	@NotNull AdmissionStatus status
) {
}
