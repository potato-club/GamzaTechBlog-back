package org.gamja.gamzatechblog.domain.admission.service;

import org.gamja.gamzatechblog.domain.admission.model.dto.CreateAdmissionResultRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.LookupRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.LookupResponse;

public interface AdmissionService {
	Long createAdmissionResult(CreateAdmissionResultRequest request);

	LookupResponse getAdmissionStatusByNameAndPhone(LookupRequest request);

	void deleteAdmissionResultById(Long admissionId);
}
