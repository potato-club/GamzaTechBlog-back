package org.gamja.gamzatechblog.domain.admission.model.mapper;

import org.gamja.gamzatechblog.domain.admission.model.dto.CreateAdmissionResultRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.LookupResponse;
import org.gamja.gamzatechblog.domain.admission.model.entity.AdmissionResult;
import org.springframework.stereotype.Component;

@Component
public class AdmissionMapper {

	public AdmissionResult toEntity(CreateAdmissionResultRequest req,
		String normalizedName,
		String normalizedPhone) {
		return AdmissionResult.builder()
			.name(req.name())
			.nameNormalized(normalizedName)
			.phoneDigits(normalizedPhone)
			.status(req.status())
			.build();
	}

	public LookupResponse toLookupResponse(AdmissionResult entity) {
		return new LookupResponse(entity.getStatus());
	}
}
