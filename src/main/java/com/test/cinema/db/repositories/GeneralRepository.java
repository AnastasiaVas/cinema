package com.test.cinema.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

@NoRepositoryBean
public interface GeneralRepository<T, ID> extends JpaRepository<T, ID>,
        JpaSpecificationExecutor<T>, PagingAndSortingRepository<T, ID> {
}
