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
import org.gamja.gamzatechblog.domain.admission.service.validator.AdmissionValidator;
import org.gamja.gamzatechblog.domain.admission.util.AdmissionNormalizer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdmissionServiceImpl implements AdmissionService {

	private final AdmissionResultRepository admissionResultRepository;
	private final AdmissionMapper admissionMapper;
	private final AdmissionValidator admissionValidator;

	@Override
	public Long createAdmissionResult(CreateAdmissionResultRequest request) {
		String nameNorm = normalizeName(request.name());
		String phoneNorm = normalizePhone(request.phone());

		admissionValidator.assertCreatable(nameNorm, phoneNorm);
		AdmissionResult entity = admissionMapper.toEntity(request, nameNorm, phoneNorm);

		try {
			return admissionResultRepository.save(entity).getId();
		} catch (DataIntegrityViolationException e) {
			throw new BusinessException(ErrorCode.ADMISSION_RESULT_DUPLICATED);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public LookupResponse getAdmissionStatusByNameAndPhone(LookupRequest request) {
		String nameNorm = normalizeName(request.name());
		String phoneNorm = normalizePhone(request.phone());

		AdmissionResult result = admissionValidator.requireByNameAndPhone(nameNorm, phoneNorm);
		return admissionMapper.toLookupResponse(result);
	}

	@Override
	public void deleteAdmissionResultById(Long admissionId) {
		AdmissionResult entity = admissionResultRepository.findById(admissionId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ADMISSION_RESULT_NOT_FOUND));
		admissionResultRepository.delete(entity);
	}

	private String normalizeName(String name) {
		return AdmissionNormalizer.normalizeName(name);
	}

	private String normalizePhone(String phone) {
		return AdmissionNormalizer.normalizePhone(phone);
	}
}
