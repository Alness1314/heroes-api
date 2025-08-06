package com.alness.gamesheroes.hero.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alness.gamesheroes.common.ResponseServer;
import com.alness.gamesheroes.hero.dto.HeroRequest;
import com.alness.gamesheroes.hero.dto.HeroResponse;
import com.alness.gamesheroes.hero.service.HeroService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/heroes")
@Tag(name = "Heroes", description = ".")
@RequiredArgsConstructor
public class HeroController {

    private final HeroService heroService;

    @GetMapping()
    public ResponseEntity<List<HeroResponse>> findAll(@RequestParam Map<String, String> parameters) {
        List<HeroResponse> response = heroService.find(parameters);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HeroResponse> findOne(@PathVariable String id) {
        HeroResponse response = heroService.findOne(id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping()
    public ResponseEntity<HeroResponse> save(@Valid @RequestBody HeroRequest request) {
        HeroResponse response = heroService.save(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HeroResponse> update(@PathVariable String id, @RequestBody HeroRequest request) {
        HeroResponse response = heroService.update(id, request);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseServer> delete(@PathVariable String id) {
        ResponseServer response = heroService.delete(id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/all")
    public ResponseEntity<ResponseServer> createAll(@RequestBody List<HeroRequest> request) {
        ResponseServer response = heroService.multiSave(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
