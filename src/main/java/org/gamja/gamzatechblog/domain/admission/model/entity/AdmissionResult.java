package org.gamja.gamzatechblog.domain.admission.model.entity;

import org.gamja.gamzatechblog.common.entity.BaseTime;
import org.gamja.gamzatechblog.domain.admission.model.type.AdmissionStatus;
import org.gamja.gamzatechblog.domain.admission.util.AdmissionNormalizer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "admission_results",
	uniqueConstraints = @UniqueConstraint(
		name = "uq_admission_name_phone",
		columnNames = {"name_normalized", "phone_digits"}
	)
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AdmissionResult extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "admission_id")
	private Long id;

	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Column(name = "name_normalized", length = 100, nullable = false)
	private String nameNormalized;

	@Column(name = "phone_digits", length = 20, nullable = false)
	private String phoneDigits;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 10, nullable = false)
	private AdmissionStatus status;

	@PrePersist
	@PreUpdate
	private void applyNormalization() {
		this.nameNormalized = AdmissionNormalizer.normalizeName(this.name);
		this.phoneDigits = this.phoneDigits == null ? null : this.phoneDigits.trim();
	}

	public boolean isPassed() {
		return this.status == AdmissionStatus.PASS;
	}

	public boolean isFailed() {
		return this.status == AdmissionStatus.FAIL;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNameNormalized(String nameNormalized) {
		this.nameNormalized = nameNormalized;
	}

	public void setPhoneDigits(String phoneDigits) {
		this.phoneDigits = phoneDigits;
	}

	public void setStatus(AdmissionStatus status) {
		this.status = status;
	}
}
