package com.test.cinema.services;

import com.test.cinema.db.repositories.GeneralRepository;
import com.test.cinema.specification.EntitySpecificationBuilder;
import com.test.cinema.specification.SearchCriteria;
import com.test.cinema.utils.enums.Entities;
import com.test.cinema.utils.enums.EntityFields;
import com.test.cinema.utils.Validation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EntityService {

    public <T> T getEntityByIdIfPresentOrThrowException(Integer id, GeneralRepository<T,
                Integer> repository, Entities entity) {
        Validation.idValidation.accept(id);
        Optional<T> t =  repository.findById(id);
        Validation.throwIfNotPresent(t, entity, EntityFields.ID, String.valueOf(id));
        return t.get();
    }

    public <T> Page<T> findEntityPageByFields(List<SearchCriteria> searchCriteria, Integer pageNum, Integer pageSize,
                                              GeneralRepository<T, Integer> repository) {
        EntitySpecificationBuilder<T> builder = new EntitySpecificationBuilder<>(searchCriteria);
        Specification<T> spec = builder.build();
        return repository.findAll(spec, PageRequest.of(pageNum, pageSize));
    }
}
