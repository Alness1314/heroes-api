package com.alness.gamesheroes.files.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import com.alness.gamesheroes.common.ResponseServer;
import com.alness.gamesheroes.files.dto.FileResponse;
import com.alness.gamesheroes.files.model.FilesEntity;
import com.alness.gamesheroes.files.repository.FileRepository;
import com.alness.gamesheroes.files.service.FileService;
import com.alness.gamesheroes.files.spec.FileSpecification;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;

    private final String baseDir = System.getProperty("user.dir") + File.separator + "assets" + File.separator;
    private final String uploadPath = baseDir + "uploads" + File.separator;

    ModelMapper mapper = new ModelMapper();

    private String messageLog = "Error message: {} caused by {}";

    @PostConstruct
    public void init() {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    @Override
    public List<FileResponse> find(Map<String, String> params) {
        Specification<FilesEntity> specification = filterWithParameters(params);
        return fileRepository.findAll(specification)
                .stream()
                .map(this::mapperDto)
                .toList();
    }

    @Override
    public FileResponse findOne(String id) {
        Map<String, String> params = Map.of("id", id);
        FilesEntity fileEntity = fileRepository.findOne(filterWithParameters(params))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Not found file with id: [%s]", id)));
        return mapperDto(fileEntity);
    }

    @Override
    public FileResponse storeFile(MultipartFile file) {
        UUID fileId = UUID.randomUUID();
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }

        String mimeType = file.getContentType();
        String fileName = fileId + "_" + originalFilename;

        try {
            Path targetLocation = Paths.get(uploadPath).resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            FilesEntity fileEntity = FilesEntity.builder()
                    .id(fileId)
                    .name(fileName)
                    .mimeType(mimeType)
                    .extension(extension)
                    .createAt(LocalDateTime.now())
                    .updateAt(LocalDateTime.now())
                    .erased(false)
                    .build();

            fileRepository.save(fileEntity);

            fileRepository.save(fileEntity);

            return mapperDto(fileEntity);
        } catch (DataIntegrityViolationException ex) {
            log.error(messageLog, ex.getMessage(), ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data integrity violation");
        } catch (Exception e) {
            log.error(messageLog, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error storing file");
        }
    }

    @Override
    public ResponseEntity<Resource> downloadFileAsResource(String id) {
        FileResponse fileResponse = this.findOne(id);
        try {
            Path filePath = Paths.get(uploadPath).resolve(fileResponse.getName()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("File not found: [%s]", fileResponse.getName()));
            }
        } catch (ResponseStatusException e) {
            log.error(messageLog, e.getMessage(), e);
            throw e;
        } catch (MalformedURLException ex) {
            log.error(messageLog, ex.getMessage(), ex);
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Error downloading file");
        } catch (Exception e) {
            log.error(messageLog, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error downloading file");
        }
    }

    public ResponseEntity<Resource> loadFileAsResource(String id) {
        FileResponse fileResponse = this.findOne(id);
        try {
            Path filePath = Paths.get(uploadPath).resolve(fileResponse.getName()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                        .body(resource);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("File not found: [%s]", fileResponse.getName()));
            }
        } catch (ResponseStatusException e) {
            log.error(messageLog, e.getMessage(), e);
            throw e;
        } catch (IOException ex) {
            log.error(messageLog, ex.getMessage(), ex);
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Error downloading file");
        } catch (Exception e) {
            log.error(messageLog, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error downloading file");
        }
    }

    private FileResponse mapperDto(FilesEntity source) {
        return mapper.map(source, FileResponse.class);
    }

    public Specification<FilesEntity> filterWithParameters(Map<String, String> parameters) {
        return new FileSpecification().getSpecificationByFilters(parameters);
    }

    @Override
    public ResponseServer deleteFile(String id) {
        // Buscar la entidad del archivo en la base de datos
        FilesEntity fileEntity = fileRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found with id: " + id));

        try {
            // Eliminar el archivo físico del sistema
            Path filePath = Paths.get(uploadPath).resolve(fileEntity.getName());
            Files.deleteIfExists(filePath);

            // Eliminar la entidad de la base de datos (borrado físico)
            fileRepository.delete(fileEntity);
            return new ResponseServer(String.format("File deleted successfully with id: %s", id), HttpStatus.ACCEPTED,
                    true);
        } catch (IOException ex) {
            log.error(messageLog, ex.getMessage(), ex);
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Error downloading file");
        } catch (Exception e) {
            log.error(messageLog, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error downloading file");
        }
    }
}
