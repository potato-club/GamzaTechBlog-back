package org.gamja.gamzatechblog.domain.admission.service;

import static org.assertj.core.api.Assertions.*;
import static org.gamja.gamzatechblog.support.admission.AdmissionFixtures.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;
import org.gamja.gamzatechblog.domain.admission.model.dto.request.CreateAdmissionResultRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.request.LookupRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.response.AdmissionResultResponse;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

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

	@Nested
	@DisplayName("getAllAdmissionResults()")
	class GetAll {

		@Test
		@DisplayName("성공: createdAt DESC 정렬로 전체 조회 및 매핑")
		void getAll_success() {
			AdmissionResult e1 = result(1L, AdmissionStatus.PASS);
			AdmissionResult e2 = result(2L, AdmissionStatus.FAIL);
			List<AdmissionResult> entities = List.of(e1, e2);

			List<AdmissionResultResponse> mapped = List.of(
				new AdmissionResultResponse(1L, NAME_RAW, PHONE, AdmissionStatus.PASS),
				new AdmissionResultResponse(2L, NAME_RAW, PHONE, AdmissionStatus.FAIL)
			);

			when(repository.findAll(any(Sort.class))).thenReturn(entities);
			when(mapper.toResultResponses(entities)).thenReturn(mapped);

			List<AdmissionResultResponse> resp = service.getAllAdmissionResults();

			assertThat(resp).hasSize(2);
			assertThat(resp.get(0).id()).isEqualTo(1L);
			assertThat(resp.get(0).name()).isEqualTo(NAME_RAW);
			assertThat(resp.get(0).phoneDigits()).isEqualTo(PHONE);
			assertThat(resp.get(0).status()).isEqualTo(AdmissionStatus.PASS);

			ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
			verify(repository).findAll(sortCaptor.capture());
			Sort sort = sortCaptor.getValue();
			assertThat(sort.getOrderFor("createdAt")).isNotNull();
			assertThat(sort.getOrderFor("createdAt").getDirection()).isEqualTo(Sort.Direction.DESC);

			verify(mapper).toResultResponses(entities);
		}

		@Test
		@DisplayName("성공: 결과가 없으면 빈 리스트 반환")
		void getAll_empty() {
			when(repository.findAll(any(Sort.class))).thenReturn(List.of());
			when(mapper.toResultResponses(List.of())).thenReturn(List.of());

			List<AdmissionResultResponse> resp = service.getAllAdmissionResults();

			assertThat(resp).isEmpty();

			ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
			verify(repository).findAll(sortCaptor.capture());
			assertThat(sortCaptor.getValue().getOrderFor("createdAt").getDirection())
				.isEqualTo(Sort.Direction.DESC);

			verify(mapper).toResultResponses(List.of());
		}
	}
}
