package org.gamja.gamzatechblog.domain.profile.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profile_images")
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_image_id")
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

}
