package com.test.cinema.services;

import com.test.cinema.db.entites.MovieEntity;
import com.test.cinema.db.repositories.MoviesRepository;
import com.test.cinema.dto.MovieDTO;
import com.test.cinema.mapper.MovieDTOMapper;
import com.test.cinema.mapper.MovieDTOMapperImpl;
import com.test.cinema.specification.SearchCriteria;
import com.test.cinema.utils.enums.Entities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static com.test.cinema.utils.enums.CreateUpdate.CREATE;
import static com.test.cinema.utils.enums.CreateUpdate.UPDATE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoviesServiceTest {

    private static Integer id;
    private static String name;
    private static String description;
    private static Integer releaseYear;
    private static Timestamp createDate;
    private static String exMessage;
    private static MovieEntity movieEntity;
    private static MovieDTO movieDTO;
    @Mock
    private MoviesRepository moviesRepository;
    @Mock
    private MovieDTOMapper movieDTOMapperMock;
    @Mock
    private EntityService entityService;
    @InjectMocks
    private MoviesService moviesService;

    @BeforeAll
    static void setUp() {
        id = 1;
        name = "Movie 1";
        description = "Movie 1 description";
        releaseYear = 1998;
        createDate = Timestamp.from(Instant.now());
        movieEntity = new MovieEntity(name, releaseYear,
                description, null, createDate, null);
        movieEntity.setId(id);
        movieDTO = new MovieDTO(id, name, releaseYear, description);
    }

    @Test
    void createOrUpdateMovie_UPDATE_SUCCESS() {

        String name = "Movie 10";
        MovieDTO updateDTO = new MovieDTO();
        updateDTO.setName(name);
        updateDTO.setId(id);
        MovieDTO expectedDTO = new MovieDTO();
        expectedDTO.setId(id);
        expectedDTO.setName("Movie 10");
        expectedDTO.setReleaseYear(releaseYear);

        when(entityService.getEntityByIdIfPresentOrThrowException(updateDTO.getId(),
                moviesRepository, Entities.MOVIE)).thenReturn(movieEntity);
        when(movieDTOMapperMock.movieDTOToMovie(updateDTO, movieEntity)).thenReturn(movieEntity);

        when(moviesRepository.save(movieEntity)).thenReturn(movieEntity);
        when(movieDTOMapperMock.movieEntityToMovieDTO(movieEntity)).thenReturn(expectedDTO);

        MovieDTO actualDTO = moviesService.createOrUpdateMovie(updateDTO, UPDATE);
        Assertions.assertEquals(expectedDTO, actualDTO);

    }

    @Test
    void createOrUpdateMovie_CREATE_SUCCESS() {

        String name = "Movie 20";
        Integer releaseYear = 2000;
        MovieDTO createDTO = new MovieDTO();
        createDTO.setName(name);
        createDTO.setReleaseYear(releaseYear);
        MovieDTO expectedDTO = new MovieDTO();
        expectedDTO.setId(2);
        expectedDTO.setName(name);
        expectedDTO.setReleaseYear(releaseYear);

        when(movieDTOMapperMock.movieDTOToMovie(createDTO, new MovieEntity())).thenReturn(movieEntity);

        when(moviesRepository.save(movieEntity)).thenReturn(movieEntity);
        when(movieDTOMapperMock.movieEntityToMovieDTO(movieEntity)).thenReturn(expectedDTO);

        MovieDTO actualDTO = moviesService.createOrUpdateMovie(createDTO, CREATE);
        Assertions.assertEquals(expectedDTO, actualDTO);

    }

    @Test
    void createOrUpdateMovie_UPDATE_NOT_FOUND() {

        exMessage = String.format("Movie with id %s wasn't found.", id);

        when(entityService
                .getEntityByIdIfPresentOrThrowException(id, moviesRepository,
                        Entities.MOVIE)).thenThrow(new EntityNotFoundException(exMessage));
        verify(movieDTOMapperMock, never()).movieEntityToMovieDTO(Mockito.any(MovieEntity.class));
        verify(moviesRepository, never()).save(Mockito.any(MovieEntity.class));

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> moviesService.createOrUpdateMovie(movieDTO, UPDATE),
                exMessage
        );

        assertTrue(thrown.getMessage().contains(exMessage));
    }

    @Test
    void deleteMovieById_SUCCESS() {

        when(entityService
                .getEntityByIdIfPresentOrThrowException(movieEntity.getId(), moviesRepository,
                        Entities.MOVIE)).thenReturn(movieEntity);
        doNothing().when(moviesRepository).delete(movieEntity);

        String actual = moviesService.deleteMovieById(id);
        String expected = String.format("Movie with id %s was successfully deleted.", id);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void deleteMovieById_NOT_FOUND() {
        exMessage = String.format("Movie with id %s wasn't found.", id);

        when(entityService
                .getEntityByIdIfPresentOrThrowException(id, moviesRepository,
                        Entities.MOVIE)).thenThrow(new EntityNotFoundException(exMessage));
        verify(movieDTOMapperMock, never()).movieEntityToMovieDTO(Mockito.any(MovieEntity.class));

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> moviesService.deleteMovieById(id),
                exMessage
        );

        assertTrue(thrown.getMessage().contains(exMessage));
    }

    @Test
    void deleteMovieById_INVALID_ID() {
        int id = -10;
        exMessage = "Id should be a positive digit.";

        when(entityService
                .getEntityByIdIfPresentOrThrowException(id, moviesRepository,
                        Entities.MOVIE)).thenThrow(new IllegalArgumentException(exMessage));
        verify(movieDTOMapperMock, never()).movieEntityToMovieDTO(Mockito.any(MovieEntity.class));

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> moviesService.deleteMovieById(id),
                exMessage
        );

        assertTrue(thrown.getMessage().contains(exMessage));
    }

    @Test
    void findMovieById_SUCCESS() {

        when(entityService
                .getEntityByIdIfPresentOrThrowException(movieEntity.getId(), moviesRepository,
                        Entities.MOVIE)).thenReturn(movieEntity);
        when(movieDTOMapperMock.movieEntityToMovieDTO(movieEntity)).thenReturn(movieDTO);
        MovieDTO actual = moviesService.findMovieById(movieEntity.getId());
        MovieDTO expected = movieDTO;
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findMovieById_NOT_FOUND() {
        exMessage = String.format("Movie with id %s wasn't found.", id);

        when(entityService
                .getEntityByIdIfPresentOrThrowException(id, moviesRepository,
                        Entities.MOVIE)).thenThrow(new EntityNotFoundException(exMessage));
        verify(movieDTOMapperMock, never()).movieEntityToMovieDTO(Mockito.any(MovieEntity.class));

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> moviesService.findMovieById(id),
                exMessage
        );

        assertTrue(thrown.getMessage().contains(exMessage));
    }

    @Test
    void findMovieById_INVALID_ID() {
        int id = -10;
        exMessage = "Id should be a positive digit.";

        when(entityService
                .getEntityByIdIfPresentOrThrowException(id, moviesRepository,
                        Entities.MOVIE)).thenThrow(new IllegalArgumentException(exMessage));
        verify(movieDTOMapperMock, never()).movieEntityToMovieDTO(Mockito.any(MovieEntity.class));

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> moviesService.findMovieById(id),
                exMessage
        );

        assertTrue(thrown.getMessage().contains(exMessage));
    }

    @ParameterizedTest
    @MethodSource("com.test.cinema.utils.Arguments#argumentsForFindMovieByCriteria")
    void findByFields(List<SearchCriteria> searchCriteria, Integer pageNum, Integer pageSize,
                      List<MovieDTO> movieDTOListExpected) {
        MovieDTOMapper movieDTOMapper = new MovieDTOMapperImpl();
        MoviesService moviesService1 = new MoviesService(moviesRepository, movieDTOMapper, entityService);
        List<MovieEntity> movieEntityList = movieDTOListExpected.stream()
                .map(movieDTOMapper::movieDTOToMovie).collect(Collectors.toList());
        Page<MovieEntity> page = new PageImpl<>(movieEntityList,
                PageRequest.of(pageNum, pageSize), 0);

        when(entityService
                .findEntityPageByFields(searchCriteria, pageNum, pageSize, moviesRepository)).thenReturn(page);

        List<MovieDTO> movieDTOListActual = moviesService1.findByFields(searchCriteria, pageNum, pageSize);

       assertEquals(movieDTOListExpected, movieDTOListActual);
    }
}