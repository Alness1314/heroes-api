package com.alness.gamesheroes.hero.service;

import java.util.List;
import java.util.Map;

import com.alness.gamesheroes.common.ResponseServer;
import com.alness.gamesheroes.hero.dto.HeroRequest;
import com.alness.gamesheroes.hero.dto.HeroResponse;

public interface HeroService {
    public List<HeroResponse> find(Map<String, String> params);
    public HeroResponse findOne(String id);
    public HeroResponse save(HeroRequest request);
    public ResponseServer multiSave(List<HeroRequest> request);
    public HeroResponse update(String id, HeroRequest request);
    public ResponseServer delete(String id);
}
