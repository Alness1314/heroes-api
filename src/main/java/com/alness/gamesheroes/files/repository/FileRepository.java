package com.alness.gamesheroes.files.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.alness.gamesheroes.files.model.FilesEntity;

public interface FileRepository extends JpaRepository<FilesEntity, UUID>, JpaSpecificationExecutor<FilesEntity> {

}
