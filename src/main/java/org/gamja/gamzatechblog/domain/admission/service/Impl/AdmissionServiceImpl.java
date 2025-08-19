package org.gamja.gamzatechblog.domain.admission.service.Impl;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;
import org.gamja.gamzatechblog.domain.admission.model.dto.CreateAdmissionResultRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.LookupRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.LookupResponse;
import org.gamja.gamzatechblog.domain.admission.model.entity.AdmissionResult;
import org.gamja.gamzatechblog.domain.admission.model.mapper.AdmissionMapper;
import org.gamja.gamzatechblog.domain.admission.repository.AdmissionResultRepository;
import org.gamja.gamzatechblog.domain.admission.service.AdmissionService;
import org.gamja.gamzatechblog.domain.admission.util.AdmissionNormalizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdmissionServiceImpl implements AdmissionService {

	private final AdmissionResultRepository admissionResultRepository;
	private final AdmissionMapper admissionMapper;

	@Override
	public Long createAdmissionResult(CreateAdmissionResultRequest request) {
		String normalizedName = AdmissionNormalizer.normalizeName(request.name());
		String normalizedPhone = AdmissionNormalizer.normalizePhone(request.phone());

		if (admissionResultRepository
			.existsByNameNormalizedAndPhoneDigits(normalizedName, normalizedPhone)) {
			throw new BusinessException(ErrorCode.ADMISSION_RESULT_DUPLICATED);
		}

		// valueOf 필요 없음
		AdmissionResult entity = admissionMapper.toEntity(request, normalizedName, normalizedPhone);
		return admissionResultRepository.save(entity).getId();
	}

	@Override
	@Transactional(readOnly = true)
	public LookupResponse getAdmissionStatusByNameAndPhone(LookupRequest request) {
		String normalizedName = AdmissionNormalizer.normalizeName(request.name());
		String normalizedPhone = AdmissionNormalizer.normalizePhone(request.phone());

		var result = admissionResultRepository
			.findByNameNormalizedAndPhoneDigits(normalizedName, normalizedPhone)
			.orElseThrow(() -> new BusinessException(ErrorCode.ADMISSION_RESULT_NOT_FOUND));

		return admissionMapper.toLookupResponse(result);
	}

	@Override
	public void deleteAdmissionResultById(Long admissionId) {
		boolean exists = admissionResultRepository.existsById(admissionId);
		if (!exists) {
			throw new BusinessException(ErrorCode.ADMISSION_RESULT_NOT_FOUND);
		}
		admissionResultRepository.deleteById(admissionId);
	}
}
