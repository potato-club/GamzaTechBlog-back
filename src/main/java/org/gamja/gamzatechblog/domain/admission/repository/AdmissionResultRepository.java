package org.gamja.gamzatechblog.domain.admission.repository;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.admission.model.entity.AdmissionResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmissionResultRepository extends JpaRepository<AdmissionResult, Long> {
	Optional<AdmissionResult> findByNameNormalizedAndPhoneDigits(String nameNormalized, String phoneDigits);

	boolean existsByNameNormalizedAndPhoneDigits(String nameNormalized, String phoneDigits);
}
