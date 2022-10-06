package com.test.cinema.services;

import com.test.cinema.db.entites.MovieEntity;
import com.test.cinema.dto.MovieDTO;
import com.test.cinema.mapper.MovieDTOMapper;
import com.test.cinema.db.repositories.MoviesRepository;
import com.test.cinema.specification.SearchCriteria;
import com.test.cinema.utils.CustomMessageGenerator;
import com.test.cinema.utils.enums.CreateUpdate;
import com.test.cinema.utils.enums.Entities;
import com.test.cinema.utils.enums.EntityFields;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MoviesService {

    private final MoviesRepository moviesRepository;
    private final MovieDTOMapper movieDTOMapper;
    private final EntityService entityService;

    public List<MovieDTO> findByFields(List<SearchCriteria> searchCriteria, Integer pageNum, Integer pageSize) {
        Page<MovieEntity> movies = entityService
                .findEntityPageByFields(searchCriteria, pageNum, pageSize, moviesRepository);
        return movies.stream()
                .map(movieDTOMapper::movieEntityToMovieDTO)
                .toList();
    }

    public MovieDTO createOrUpdateMovie(MovieDTO movieDTO, CreateUpdate createUpdate) {
        MovieEntity movieEntity = getMovieForCreateOrUpdate(createUpdate, movieDTO.getId());

        return movieDTOMapper.movieEntityToMovieDTO(moviesRepository
                .save(movieDTOMapper.movieDTOToMovie(movieDTO, movieEntity)));
    }

    public String deleteMovieById(Integer movieId) {
        MovieEntity movieEntity = entityService.getEntityByIdIfPresentOrThrowException(movieId,
                moviesRepository, Entities.MOVIE);
        moviesRepository.delete(movieEntity);
        return CustomMessageGenerator.entityDeleted(Entities.MOVIE, EntityFields.ID, String.valueOf(movieId));
    }

    public MovieDTO findMovieById(Integer movieId) {
        MovieEntity movieEntity = entityService
                .getEntityByIdIfPresentOrThrowException(movieId, moviesRepository, Entities.MOVIE);
        return movieDTOMapper.movieEntityToMovieDTO(movieEntity);
    }

    private MovieEntity getMovieForCreateOrUpdate(CreateUpdate createUpdate, Integer movieId) {
        if (createUpdate == CreateUpdate.UPDATE) {
            return entityService.getEntityByIdIfPresentOrThrowException(movieId,
                    moviesRepository, Entities.MOVIE);
        }
        return new MovieEntity();
    }
}
