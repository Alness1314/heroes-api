package com.alness.gamesheroes.hero.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.alness.gamesheroes.hero.model.Hero;

public interface HeroRepository extends JpaRepository<Hero, UUID>, JpaSpecificationExecutor<Hero>{
    
}
