package com.alness.gamesheroes.hero.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.alness.gamesheroes.files.dto.FileResponse;

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
public class HeroResponse {
    private UUID id;
    private String name;
    private String nickname;
    private String franchise;
    private String companyOrigin;
    private String debutGame;
    private Integer yearDebut;
    private String notes;
    private FileResponse image;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Boolean erased;
}
