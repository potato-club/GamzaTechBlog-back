package org.gamja.gamzatechblog.support.admission;

import org.gamja.gamzatechblog.domain.admission.model.dto.request.CreateAdmissionResultRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.request.LookupRequest;
import org.gamja.gamzatechblog.domain.admission.model.entity.AdmissionResult;
import org.gamja.gamzatechblog.domain.admission.model.type.AdmissionStatus;
import org.gamja.gamzatechblog.domain.admission.util.AdmissionNormalizer;

public final class AdmissionFixtures {
	private AdmissionFixtures() {
	}

	public static final String NAME_RAW = " 홍길동 ";
	public static final String NAME_NORM = AdmissionNormalizer.normalizeName(NAME_RAW);
	public static final String PHONE = "01012341234";

	public static CreateAdmissionResultRequest createReqPass() {
		return new CreateAdmissionResultRequest(NAME_RAW, PHONE, AdmissionStatus.PASS);
	}

	public static CreateAdmissionResultRequest createReqFail() {
		return new CreateAdmissionResultRequest(NAME_RAW, PHONE, AdmissionStatus.FAIL);
	}

	public static LookupRequest lookupReq() {
		return new LookupRequest(NAME_RAW, PHONE);
	}

	public static AdmissionResult result(Long id, AdmissionStatus status) {
		return AdmissionResult.builder()
			.id(id)
			.name(NAME_RAW)
			.nameNormalized(NAME_NORM)
			.phoneDigits(PHONE)
			.status(status)
			.build();
	}
}
