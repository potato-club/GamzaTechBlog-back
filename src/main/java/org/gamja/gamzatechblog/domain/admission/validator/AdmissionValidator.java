package org.gamja.gamzatechblog.domain.admission.service.validator;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;
import org.gamja.gamzatechblog.domain.admission.model.entity.AdmissionResult;
import org.gamja.gamzatechblog.domain.admission.repository.AdmissionResultRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdmissionValidator {

	private final AdmissionResultRepository admissionResultRepository;

	public void assertCreatable(String nameNorm, String phoneNorm) {
		boolean exists = admissionResultRepository
			.existsByNameNormalizedAndPhoneDigits(nameNorm, phoneNorm);
		if (exists) {
			throw new BusinessException(ErrorCode.ADMISSION_RESULT_DUPLICATED);
		}
	}

	public AdmissionResult requireByNameAndPhone(String nameNorm, String phoneNorm) {
		return admissionResultRepository
			.findByNameNormalizedAndPhoneDigits(nameNorm, phoneNorm)
			.orElseThrow(() -> new BusinessException(ErrorCode.ADMISSION_RESULT_NOT_FOUND));
	}
}
