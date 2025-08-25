package org.gamja.gamzatechblog.domain.admission.model.dto.request;

import org.gamja.gamzatechblog.domain.admission.model.type.AdmissionStatus;

public record UpdateAdmissionResultRequest(
	String name,
	String phone,
	AdmissionStatus status
) {
}
