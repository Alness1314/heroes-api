package com.alness.gamesheroes.files.spec;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import com.alness.gamesheroes.files.model.FilesEntity;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class FileSpecification implements Specification<FilesEntity> {

    @SuppressWarnings("null")
    @Override
    @Nullable
    public Predicate toPredicate(Root<FilesEntity> arg0, @Nullable CriteriaQuery<?> arg1, CriteriaBuilder arg2) {
        return null;
    }

    public Specification<FilesEntity> getSpecificationByFilters(Map<String, String> params) {
        Specification<FilesEntity> specification = null;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            Specification<FilesEntity> currentFilter = switch (entry.getKey()) {
                case "id" -> filterById(entry.getValue());
                case "name" -> filterByName(entry.getValue());
                case "erased" -> filterByErased(entry.getValue());
                default -> null;
            };

            if (currentFilter != null) {
                specification = (specification == null)
                        ? currentFilter
                        : specification.and(currentFilter);
            }
        }
        return specification;
    }

    private Specification<FilesEntity> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<FilesEntity> filterByName(String name) {
        return (root, query, cb) -> cb.equal(root.<String>get("name"), name);
    }

    private Specification<FilesEntity> filterByErased(String erase) {
        return (root, query, cb) -> cb.equal(root.<Boolean>get("erased"), Boolean.parseBoolean(erase));
    }

}
