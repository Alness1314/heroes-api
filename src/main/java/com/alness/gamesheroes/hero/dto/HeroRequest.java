package com.alness.gamesheroes.hero.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeroRequest {
    private String name;
    private String nickname;
    private String franchise;
    private String companyOrigin;
    private String debutGame;
    private Integer yearDebut;
    private String notes;
    private String imageId;
}
