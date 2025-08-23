package org.gamja.gamzatechblog.domain.admission.model.dto.response;

import org.gamja.gamzatechblog.domain.admission.model.type.AdmissionStatus;

public record AdmissionResultResponse(
	Long id,
	String name,
	String phoneDigits,
	AdmissionStatus status
) {
}


