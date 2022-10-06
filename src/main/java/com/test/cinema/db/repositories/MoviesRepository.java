package com.test.cinema.db.repositories;

import com.test.cinema.db.entites.MovieEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface MoviesRepository  extends GeneralRepository<MovieEntity, Integer> {
}
