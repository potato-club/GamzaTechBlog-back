package org.gamja.gamzatechblog.domain.admission.model.mapper;

import java.util.List;

import org.gamja.gamzatechblog.domain.admission.model.dto.request.CreateAdmissionResultRequest;
import org.gamja.gamzatechblog.domain.admission.model.dto.response.AdmissionResultResponse;
import org.gamja.gamzatechblog.domain.admission.model.dto.response.LookupResponse;
import org.gamja.gamzatechblog.domain.admission.model.entity.AdmissionResult;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
	componentModel = "spring",
	injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface AdmissionMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "name", source = "request.name")
	@Mapping(target = "nameNormalized", source = "normalizedName")
	@Mapping(target = "phoneDigits", source = "normalizedPhone")
	@Mapping(target = "status", source = "request.status")
	AdmissionResult toEntity(CreateAdmissionResultRequest request,
		String normalizedName,
		String normalizedPhone);

	List<AdmissionResultResponse> toResultResponses(List<AdmissionResult> entities);

	default LookupResponse toLookupResponse(AdmissionResult entity) {
		return new LookupResponse(entity.getStatus());
	}
}
