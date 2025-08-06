package com.alness.gamesheroes.hero.spec;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import com.alness.gamesheroes.hero.model.Hero;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class HeroSpec implements Specification<Hero> {

    @SuppressWarnings("null")
    @Override
    @Nullable
    public Predicate toPredicate(Root<Hero> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return null;
    }

    public Specification<Hero> getSpecificationByFilters(Map<String, String> params) {
        Specification<Hero> specification = null;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            Specification<Hero> currentFilter = switch (entry.getKey()) {
                case "id" -> filterById(entry.getValue());
                case "name" -> filterByName(entry.getValue());
                case "company" -> filterByCompany(entry.getValue());
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

    private Specification<Hero> filterById(String id) {
        return (root, query, cb) -> cb.equal(root.<UUID>get("id"), UUID.fromString(id));
    }

    private Specification<Hero> filterByName(String name) {
        return (root, query, cb) -> cb.equal(root.<String>get("name"), name);

    }

    private Specification<Hero> filterByCompany(String company) {
        return (root, query, cb) -> cb.equal(root.<String>get("companyOrigin"), company);
    }

}
