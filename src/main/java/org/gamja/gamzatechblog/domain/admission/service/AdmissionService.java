package org.gamja.gamzatechblog.domain.admission.service;

import java.util.List;

import org.gamja.gamzatechblog.domain.admission.model.dto.request.CreateAdmissionResultRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.request.LookupRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.response.AdmissionResultResponse;
import org.gamja.gamzatechblog.domain.admission.model.dto.response.LookupResponse;

public interface AdmissionService {
	Long createAdmissionResult(CreateAdmissionResultRequest request);

	LookupResponse getAdmissionStatusByNameAndPhone(LookupRequest request);

	void deleteAdmissionResultById(Long admissionId);

	List<AdmissionResultResponse> getAllAdmissionResults();

}
