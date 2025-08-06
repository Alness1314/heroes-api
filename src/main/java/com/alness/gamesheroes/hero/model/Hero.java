package com.alness.gamesheroes.hero.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.alness.gamesheroes.files.model.FilesEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "heroes")
@Data
public class Hero {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "name", nullable = false, columnDefinition = "character varying(128)")
    private String name;

    @Column(name = "nickname", nullable = true, columnDefinition = "character varying(128)")
    private String nickname;

    @Column(name = "franchise", nullable = false, columnDefinition = "character varying(256)")
    private String franchise;

    @Column(name = "company_origin", nullable = false, columnDefinition = "character varying(128)")
    private String companyOrigin;

    @Column(name = "debut_game", nullable = false, columnDefinition = "character varying(256)")
    private String debutGame;

    @Column(name = "year_debut", nullable = false, columnDefinition = "bigint")
    private Integer yearDebut;

    @Column(name = "notes", nullable = true, columnDefinition = "text")
    private String notes;

    @OneToOne
    @JoinColumn(name = "image_id", nullable = true)
    private FilesEntity image;

    @Column(name = "create_at", nullable = false, columnDefinition = "timestamp without time zone")
    private LocalDateTime createAt;

    @Column(name = "update_at", nullable = false, updatable = true, columnDefinition = "timestamp without time zone")
    private LocalDateTime updateAt;

    @Column(nullable = false, columnDefinition = "boolean")
    private Boolean erased;

    @PrePersist()
    public void init() {
        setCreateAt(LocalDateTime.now());
        setUpdateAt(LocalDateTime.now());
        setErased(false);
    }

    @PreUpdate
    private void preUpdate() {
        setUpdateAt(LocalDateTime.now());
    }
}
