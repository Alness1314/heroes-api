package com.alness.gamesheroes.hero.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.alness.gamesheroes.common.ResponseServer;
import com.alness.gamesheroes.files.model.FilesEntity;
import com.alness.gamesheroes.files.repository.FileRepository;
import com.alness.gamesheroes.hero.dto.HeroRequest;
import com.alness.gamesheroes.hero.dto.HeroResponse;
import com.alness.gamesheroes.hero.model.Hero;
import com.alness.gamesheroes.hero.repository.HeroRepository;
import com.alness.gamesheroes.hero.service.HeroService;
import com.alness.gamesheroes.hero.spec.HeroSpec;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeroServiceImpl implements HeroService {
    private final HeroRepository heroRepository;
    private final FileRepository fileRepository;

    private ModelMapper mapper = new ModelMapper();

    @PostConstruct
    private void initMapper() {
        mapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public List<HeroResponse> find(Map<String, String> params) {
        Specification<Hero> specification = filterWithParameters(params);
        return heroRepository.findAll(specification)
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public HeroResponse findOne(String id) {
        Hero hero = heroRepository.findOne(filterWithParameters(Map.of("id", id)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Hero with id: [%s] not found", id)));
        return mapperDto(hero);
    }

    @Override
    public HeroResponse save(HeroRequest request) {
        Hero hero = mapper.map(request, Hero.class);
        try {
            if (request.getImageId() != null) {
                FilesEntity imageFile = fileRepository.findById(UUID.fromString(request.getImageId()))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                String.format("File with id: [%s] not found", request.getImageId())));
                hero.setImage(imageFile);
            }
            hero = heroRepository.save(hero);
        } catch (DataIntegrityViolationException ex) {
            log.error("Error saving hero: {}", ex.getMessage(), ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Data integrity error to save");
        } catch (Exception e) {
            log.error("Error saving hero: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unknown error when saving");
        }
        return mapperDto(hero);
    }

    @Override
    public HeroResponse update(String id, HeroRequest request) {
        try {
            // Buscar el héroe existente
            Hero hero = heroRepository.findById(UUID.fromString(id))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("Hero with id: [%s] not found", id)));

            // Actualizar campos desde el request
            mapper.map(request, hero);

            // Validar y setear imagen si se proporciona
            if (request.getImageId() != null) {
                FilesEntity imageFile = fileRepository.findById(UUID.fromString(request.getImageId()))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                String.format("File with id: [%s] not found", request.getImageId())));
                hero.setImage(imageFile);
            } else {
                hero.setImage(null); // opcional: limpiar si no se envía
            }

            // Guardar actualización
            hero = heroRepository.save(hero);

            return mapperDto(hero);
        } catch (DataIntegrityViolationException ex) {
            log.error("Error updating hero: {}", ex.getMessage(), ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Data integrity error during update");
        } catch (Exception e) {
            log.error("Unexpected error updating hero: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unknown error when updating");
        }
    }

    private HeroResponse mapperDto(Hero source) {
        return mapper.map(source, HeroResponse.class);
    }

    public Specification<Hero> filterWithParameters(Map<String, String> parameters) {
        return new HeroSpec().getSpecificationByFilters(parameters);
    }

    @Override
    public ResponseServer delete(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public ResponseServer multiSave(List<HeroRequest> request) {
        List<Map<String, Object>> response = new ArrayList<>();
        request.forEach(item -> {
            HeroResponse resp = save(item);
            if (resp != null) {
                response.add(Map.of("hero", resp.getName(), "status", true));
            } else {
                response.add(Map.of("hero", item.getName(), "status", false));
            }
        });
        return new ResponseServer("Heroes saved successfully", HttpStatus.ACCEPTED, true, Map.of("data", response));
    }

}
