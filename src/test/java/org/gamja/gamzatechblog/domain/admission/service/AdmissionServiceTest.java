package org.gamja.gamzatechblog.domain.admission.service;

import static org.assertj.core.api.Assertions.*;
import static org.gamja.gamzatechblog.support.admission.AdmissionFixtures.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;
import org.gamja.gamzatechblog.domain.admission.model.dto.request.CreateAdmissionResultRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.request.LookupRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.response.LookupResponse;
import org.gamja.gamzatechblog.domain.admission.model.entity.AdmissionResult;
import org.gamja.gamzatechblog.domain.admission.model.mapper.AdmissionMapper;
import org.gamja.gamzatechblog.domain.admission.model.type.AdmissionStatus;
import org.gamja.gamzatechblog.domain.admission.repository.AdmissionResultRepository;
import org.gamja.gamzatechblog.domain.admission.service.Impl.AdmissionServiceImpl;
import org.gamja.gamzatechblog.domain.admission.validator.AdmissionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@DisplayName("AdmissionService 메서드 단위 테스트")
@ExtendWith(MockitoExtension.class)
class AdmissionServiceTest {

	@Mock
	private AdmissionResultRepository repository;
	@Mock
	private AdmissionMapper mapper;
	@Mock
	private AdmissionValidator validator;

	private AdmissionService service;

	@BeforeEach
	void setUp() {
		service = new AdmissionServiceImpl(repository, mapper, validator);
	}

	@Nested
	@DisplayName("createAdmissionResult()")
	class Create {

		@Test
		@DisplayName("성공: 정규화 → 중복검증 통과 → 저장 후 ID 반환")
		void create_success() {
			// given
			CreateAdmissionResultRequest req = createReqPass();
			AdmissionResult toSave = result(null, AdmissionStatus.PASS);
			AdmissionResult saved = result(1L, AdmissionStatus.PASS);

			when(mapper.toEntity(eq(req), eq(NAME_NORM), eq(PHONE))).thenReturn(toSave);
			when(repository.save(toSave)).thenReturn(saved);

			// when
			Long id = service.createAdmissionResult(req);

			// then
			assertThat(id).isEqualTo(1L);
			verify(validator).assertCreatable(NAME_NORM, PHONE);
			verify(repository).save(toSave);
		}

		@Test
		@DisplayName("실패: 사전 중복검증에서 DUPLICATED 예외")
		void create_duplicated_by_precheck() {
			// given
			CreateAdmissionResultRequest req = createReqPass();
			doThrow(new BusinessException(ErrorCode.ADMISSION_RESULT_DUPLICATED))
				.when(validator).assertCreatable(NAME_NORM, PHONE);

			// when / then
			BusinessException ex = catchThrowableOfType(() -> service.createAdmissionResult(req),
				BusinessException.class);
			assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ADMISSION_RESULT_DUPLICATED);
			verify(repository, never()).save(any());
		}

		@Test
		@DisplayName("실패: 저장 시 유니크 위반 → DUPLICATED 매핑")
		void create_unique_violation_on_save() {
			// given
			CreateAdmissionResultRequest req = createReqFail();
			AdmissionResult toSave = result(null, AdmissionStatus.FAIL);

			when(mapper.toEntity(eq(req), eq(NAME_NORM), eq(PHONE))).thenReturn(toSave);
			when(repository.save(toSave)).thenThrow(new DataIntegrityViolationException("dup"));

			// when / then
			BusinessException ex = catchThrowableOfType(() -> service.createAdmissionResult(req),
				BusinessException.class);
			assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ADMISSION_RESULT_DUPLICATED);
		}
	}

	@Nested
	@DisplayName("getAdmissionStatusByNameAndPhone()")
	class Lookup {

		@Test
		@DisplayName("성공: 정규화된 값으로 조회 → PASS 응답")
		void lookup_success() {
			// given
			LookupRequest req = lookupReq();
			AdmissionResult found = result(7L, AdmissionStatus.PASS);

			when(validator.requireByNameAndPhone(NAME_NORM, PHONE)).thenReturn(found);
			when(mapper.toLookupResponse(found)).thenReturn(new LookupResponse(AdmissionStatus.PASS));

			// when
			LookupResponse resp = service.getAdmissionStatusByNameAndPhone(req);

			// then
			assertThat(resp.status()).isEqualTo(AdmissionStatus.PASS);
			verify(validator).requireByNameAndPhone(NAME_NORM, PHONE);
		}

		@Test
		@DisplayName("실패: 결과 없음 → NOT_FOUND")
		void lookup_not_found() {
			// given
			LookupRequest req = lookupReq();
			doThrow(new BusinessException(ErrorCode.ADMISSION_RESULT_NOT_FOUND))
				.when(validator).requireByNameAndPhone(NAME_NORM, PHONE);

			// when / then
			BusinessException ex = catchThrowableOfType(() -> service.getAdmissionStatusByNameAndPhone(req),
				BusinessException.class);
			assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ADMISSION_RESULT_NOT_FOUND);
		}
	}

	@Nested
	@DisplayName("deleteAdmissionResultById()")
	class Delete {

		@Test
		@DisplayName("성공: 엔티티 존재 시 삭제")
		void delete_success() {
			// given
			AdmissionResult entity = result(10L, AdmissionStatus.FAIL);
			when(repository.findById(10L)).thenReturn(Optional.of(entity));

			// when
			service.deleteAdmissionResultById(10L);

			// then
			verify(repository).delete(entity);
		}

		@Test
		@DisplayName("실패: 엔티티 미존재 → NOT_FOUND")
		void delete_not_found() {
			// given
			when(repository.findById(99L)).thenReturn(Optional.empty());

			// when / then
			BusinessException ex = catchThrowableOfType(() -> service.deleteAdmissionResultById(99L),
				BusinessException.class);
			assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ADMISSION_RESULT_NOT_FOUND);
			verify(repository, never()).delete(any());
		}
	}
}
