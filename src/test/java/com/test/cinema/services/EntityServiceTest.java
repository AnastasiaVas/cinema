package com.test.cinema.services;

import com.test.cinema.db.entites.MovieEntity;
import com.test.cinema.db.entites.OrderEntity;
import com.test.cinema.db.repositories.MoviesRepository;
import com.test.cinema.db.repositories.OrdersRepository;
import com.test.cinema.dto.MovieDTO;
import com.test.cinema.dto.OrderDTO;
import com.test.cinema.specification.SearchCriteria;
import com.test.cinema.utils.enums.Entities;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntityServiceTest {

    private static Integer id;
    private static MovieEntity movieEntity;
    private static OrderEntity orderEntity;

    private static Integer ticketQuantity;
    private static Timestamp createDate;
    private static OrderDTO orderDTO;
    private static MovieDTO movieDTO;
    private static String exMessage;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private MoviesRepository moviesRepository;

    @InjectMocks
    private EntityService entityService;

    @BeforeAll
    static void setUp() {
        id = 1;
        String name = "Movie 1";
        String description = "Movie 1 description";
        Integer releaseYear = 1998;
        ticketQuantity = 3;
        createDate = Timestamp.from(Instant.now());
        movieEntity = new MovieEntity(name, releaseYear,
                description, null, createDate, null);
        movieEntity.setId(id);
        movieDTO = new MovieDTO(id, name, releaseYear, description);

        orderEntity = new OrderEntity(movieEntity, ticketQuantity, createDate, null);
        orderEntity.setId(id);
        orderDTO = new OrderDTO(id, id, ticketQuantity);
    }

    @Test
    void getEntityByIdIfPresentOrThrowException_SUCCESS_MOVIE() {
        when(moviesRepository.findById(id)).thenReturn(Optional.of(movieEntity));

        assertEquals(movieEntity, entityService
                .getEntityByIdIfPresentOrThrowException(id, moviesRepository, Entities.MOVIE));
    }

    @Test
    void getEntityByIdIfPresentOrThrowException_SUCCESS_ORDER() {
        when(ordersRepository.findById(id)).thenReturn(Optional.of(orderEntity));

        assertEquals(orderEntity, entityService
                .getEntityByIdIfPresentOrThrowException(id, ordersRepository, Entities.ORDER));
    }

    @Test
    void getEntityByIdIfPresentOrThrowException_ID_INVALID() {
        int id = 0;
        exMessage = "Id should be a positive digit.";

        verify(moviesRepository, never()).findById(id);

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> entityService.getEntityByIdIfPresentOrThrowException(id,
                        moviesRepository, Entities.MOVIE),
                exMessage
        );

        assertTrue(thrown.getMessage().contains(exMessage));
    }

    @Test
    void getEntityByIdIfPresentOrThrowException_NOT_FOUND() {
        exMessage = String.format("Movie with id %s doesn't exist", id);

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> entityService.getEntityByIdIfPresentOrThrowException(id,
                        moviesRepository, Entities.MOVIE),
                exMessage
        );

        assertTrue(thrown.getMessage().contains(exMessage));
    }

    @Test
    void findEntityPageByFields_ORDER_SUCCESS() {
        int pageNum = 0;
        int pageSize = 10;
        List<OrderEntity> orderEntityList = new ArrayList<>();
        orderEntityList.add(orderEntity);
        Page<OrderEntity> page = new PageImpl<>(orderEntityList,
                PageRequest.of(pageNum, pageSize), 0);
        List<SearchCriteria> searchCriteria = new ArrayList<>();
        searchCriteria.add(new SearchCriteria("ticketQuantity",
                "=", ticketQuantity, false));

        when(ordersRepository.findAll(Mockito.any(Specification.class),
                Mockito.any(Pageable.class))).thenReturn(page);

        Page<OrderEntity> expected = page;
        Page<OrderEntity> actual = entityService.findEntityPageByFields(searchCriteria,
                pageNum, pageSize, ordersRepository);
        assertEquals(expected, actual);
    }

    @Test
    void findEntityPageByFields_MOVIE_SUCCESS() {
        int pageNum = 0;
        int pageSize = 10;
        List<MovieEntity> movieEntityList = new ArrayList<>();
        movieEntityList.add(movieEntity);
        Page<MovieEntity> page = new PageImpl<>(movieEntityList,
                PageRequest.of(pageNum, pageSize), 0);
        List<SearchCriteria> searchCriteria = new ArrayList<>();
        searchCriteria.add(new SearchCriteria("name",
                "=", "Movie 1", false));

        when(moviesRepository.findAll(Mockito.any(Specification.class),
                Mockito.any(Pageable.class))).thenReturn(page);

        Page<MovieEntity> expected = page;
        Page<MovieEntity> actual = entityService.findEntityPageByFields(searchCriteria,
                pageNum, pageSize, moviesRepository);
        assertEquals(expected, actual);
    }
}