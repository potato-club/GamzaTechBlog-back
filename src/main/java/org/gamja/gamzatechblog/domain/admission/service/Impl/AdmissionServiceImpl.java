package org.gamja.gamzatechblog.domain.admission.service.Impl;

import java.util.List;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;
import org.gamja.gamzatechblog.domain.admission.model.dto.request.CreateAdmissionResultRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.request.LookupRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.request.UpdateAdmissionResultRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.response.AdmissionResultResponse;
import org.gamja.gamzatechblog.domain.admission.model.dto.response.LookupResponse;
import org.gamja.gamzatechblog.domain.admission.model.entity.AdmissionResult;
import org.gamja.gamzatechblog.domain.admission.model.mapper.AdmissionMapper;
import org.gamja.gamzatechblog.domain.admission.repository.AdmissionResultRepository;
import org.gamja.gamzatechblog.domain.admission.service.AdmissionService;
import org.gamja.gamzatechblog.domain.admission.util.AdmissionNormalizer;
import org.gamja.gamzatechblog.domain.admission.validator.AdmissionValidator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
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

	@Override
	@Transactional(readOnly = true)
	public List<AdmissionResultResponse> getAllAdmissionResults() {
		return admissionMapper.toResultResponses(
			admissionResultRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")));
	}

	@Override
	public Long updateAdmissionResult(Long admissionId, UpdateAdmissionResultRequest request) {
		AdmissionResult entity = admissionResultRepository.findById(admissionId)
			.orElseThrow(() -> new BusinessException(ErrorCode.ADMISSION_RESULT_NOT_FOUND));

		String nameNorm = request.name() != null ? normalizeName(request.name()) : null;
		String phoneNorm = request.phone() != null ? normalizePhone(request.phone()) : null;

		admissionMapper.updateEntity(entity, request, nameNorm, phoneNorm);

		try {
			return admissionResultRepository.save(entity).getId();
		} catch (DataIntegrityViolationException e) {
			throw new BusinessException(ErrorCode.ADMISSION_RESULT_DUPLICATED);
		}
	}

	private String normalizeName(String name) {
		return AdmissionNormalizer.normalizeName(name);
	}

	private String normalizePhone(String phone) {
		return AdmissionNormalizer.normalizePhone(phone);
	}
}
