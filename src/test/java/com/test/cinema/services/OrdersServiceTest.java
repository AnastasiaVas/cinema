package com.test.cinema.services;

import com.test.cinema.db.entites.MovieEntity;
import com.test.cinema.db.entites.OrderEntity;
import com.test.cinema.db.repositories.MoviesRepository;
import com.test.cinema.db.repositories.OrdersRepository;
import com.test.cinema.dto.MovieDTO;
import com.test.cinema.dto.OrderDTO;
import com.test.cinema.mapper.OrderDTOMapper;
import com.test.cinema.mapper.OrderDTOMapperImpl;
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
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    private static Integer id;
    private static MovieEntity movieEntity;
    private static OrderEntity orderEntity;

    private static Integer ticketQuantity;
    private static Long orderNumber;
    private static Timestamp createDate;
    private static OrderDTO orderDTO;
    private static MovieDTO movieDTO;
    private static String exMessage;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private MoviesRepository moviesRepository;
    @Mock
    private OrderDTOMapper orderDTOMapperMock;
    @Mock
    private EntityService entityService;
    @InjectMocks
    private OrdersService ordersService;

    @BeforeAll
    static void setUp() {
        id = 1;
        String name = "Order 1";
        String description = "Order 1 description";
        Integer releaseYear = 1998;
        ticketQuantity = 3;
        createDate = Timestamp.from(Instant.now());
        movieEntity = new MovieEntity(name, releaseYear,
                description, null, createDate, null);
        movieEntity.setId(id);
        movieDTO = new MovieDTO(id, name, releaseYear, description);

        orderEntity = new OrderEntity(movieEntity, ticketQuantity,
                orderNumber, createDate, null);
        orderEntity.setId(id);
        orderDTO = new OrderDTO(id, id, ticketQuantity, orderNumber);
    }

    @Test
    void findOrderById_SUCCESS() {

        when(entityService
                .getEntityByIdIfPresentOrThrowException(orderEntity.getId(), ordersRepository,
                        Entities.ORDER)).thenReturn(orderEntity);
        when(orderDTOMapperMock.orderEntityToOrderDTO(orderEntity)).thenReturn(orderDTO);
        OrderDTO actual = ordersService.findOrderById(orderEntity.getId());
        OrderDTO expected = orderDTO;
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findOrderById_NOT_FOUND() {
        exMessage = String.format("Order with id %s wasn't found.", id);

        when(entityService
                .getEntityByIdIfPresentOrThrowException(id, ordersRepository,
                        Entities.ORDER)).thenThrow(new EntityNotFoundException(exMessage));
        verify(orderDTOMapperMock, never()).orderEntityToOrderDTO(Mockito.any(OrderEntity.class));

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> ordersService.findOrderById(id),
                exMessage
        );

        assertTrue(thrown.getMessage().contains(exMessage));
    }

    @Test
    void findOrderById_INVALID_ID() {
        int id = -10;
        exMessage = "Id should be a positive digit.";

        when(entityService
                .getEntityByIdIfPresentOrThrowException(id, ordersRepository,
                        Entities.ORDER)).thenThrow(new IllegalArgumentException(exMessage));
        verify(orderDTOMapperMock, never()).orderEntityToOrderDTO(Mockito.any(OrderEntity.class));

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> ordersService.findOrderById(id),
                exMessage
        );

        assertTrue(thrown.getMessage().contains(exMessage));
    }

    @Test
    void createOrUpdateOrder_UPDATE_SUCCESS() {

        Integer ticketQuantity = 10;
        OrderDTO updateDTO = new OrderDTO();
        updateDTO.setTicketQuantity(ticketQuantity);
        updateDTO.setId(id);
        OrderDTO expectedDTO = new OrderDTO();
        expectedDTO.setId(id);
        expectedDTO.setTicketQuantity(ticketQuantity);
        expectedDTO.setMovieId(id);

        when(entityService.getEntityByIdIfPresentOrThrowException(updateDTO.getId(),
                ordersRepository, Entities.ORDER)).thenReturn(orderEntity);
        when(orderDTOMapperMock.orderDTOToOrder(updateDTO, orderEntity)).thenReturn(orderEntity);

        when(ordersRepository.save(orderEntity)).thenReturn(orderEntity);
        when(orderDTOMapperMock.orderEntityToOrderDTO(orderEntity)).thenReturn(expectedDTO);

        OrderDTO actualDTO = ordersService.createOrUpdateOrder(updateDTO, UPDATE);
        Assertions.assertEquals(expectedDTO, actualDTO);

    }

    @Test
    void createOrUpdateOrder_CREATE_SUCCESS() {

        Integer ticketQuantity = 10;
        OrderDTO createDTO = new OrderDTO();
        createDTO.setTicketQuantity(ticketQuantity);
        createDTO.setMovieId(id);
        OrderDTO expectedDTO = new OrderDTO();
        expectedDTO.setId(id);
        expectedDTO.setTicketQuantity(ticketQuantity);
        expectedDTO.setMovieId(id);


        when(orderDTOMapperMock.orderDTOToOrder(createDTO, new OrderEntity())).thenReturn(orderEntity);

        when(ordersRepository.save(orderEntity)).thenReturn(orderEntity);
        when(orderDTOMapperMock.orderEntityToOrderDTO(orderEntity)).thenReturn(expectedDTO);

        OrderDTO actualDTO = ordersService.createOrUpdateOrder(createDTO, CREATE);
        Assertions.assertEquals(expectedDTO, actualDTO);

    }

    @Test
    void createOrUpdateOrder_UPDATE_NOT_FOUND() {

        exMessage = String.format("Order with id %s wasn't found.", id);

        when(entityService
                .getEntityByIdIfPresentOrThrowException(id, ordersRepository,
                        Entities.ORDER)).thenThrow(new EntityNotFoundException(exMessage));
        verify(orderDTOMapperMock, never()).orderEntityToOrderDTO(Mockito.any(OrderEntity.class));
        verify(ordersRepository, never()).save(Mockito.any(OrderEntity.class));

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> ordersService.createOrUpdateOrder(orderDTO, UPDATE),
                exMessage
        );

        assertTrue(thrown.getMessage().contains(exMessage));
    }

    @Test
    void createOrUpdateOrder_MOVIE_NOT_FOUND() {

        exMessage = String.format("Movie with id %s wasn't found.", id);

        when(entityService
                .getEntityByIdIfPresentOrThrowException(id, ordersRepository,
                        Entities.ORDER)).thenThrow(new EntityNotFoundException(exMessage));
        verify(orderDTOMapperMock, never()).orderEntityToOrderDTO(Mockito.any(OrderEntity.class));
        verify(ordersRepository, never()).save(Mockito.any(OrderEntity.class));

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> ordersService.createOrUpdateOrder(orderDTO, UPDATE),
                exMessage
        );

        assertTrue(thrown.getMessage().contains(exMessage));
    }

    @Test
    void deleteOrderById_SUCCESS() {

        when(entityService
                .getEntityByIdIfPresentOrThrowException(orderEntity.getId(), ordersRepository,
                        Entities.ORDER)).thenReturn(orderEntity);
        doNothing().when(ordersRepository).delete(orderEntity);

        String actual = ordersService.deleteOrderById(id);
        String expected = String.format("Order with id %s was successfully deleted.", id);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void deleteOrderById_NOT_FOUND() {
        exMessage = String.format("Order with id %s wasn't found.", id);

        when(entityService
                .getEntityByIdIfPresentOrThrowException(id, ordersRepository,
                        Entities.ORDER)).thenThrow(new EntityNotFoundException(exMessage));
        verify(orderDTOMapperMock, never()).orderEntityToOrderDTO(Mockito.any(OrderEntity.class));

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> ordersService.deleteOrderById(id),
                exMessage
        );

        assertTrue(thrown.getMessage().contains(exMessage));
    }

    @Test
    void deleteOrderById_INVALID_ID() {
        int id = -10;
        exMessage = "Id should be a positive digit.";

        when(entityService
                .getEntityByIdIfPresentOrThrowException(id, ordersRepository,
                        Entities.ORDER)).thenThrow(new IllegalArgumentException(exMessage));
        verify(orderDTOMapperMock, never()).orderEntityToOrderDTO(Mockito.any(OrderEntity.class));

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> ordersService.deleteOrderById(id),
                exMessage
        );

        assertTrue(thrown.getMessage().contains(exMessage));
    }

    @ParameterizedTest
    @MethodSource("com.test.cinema.utils.Arguments#argumentsForFindOrderByCriteria")
    void findByFields(List<SearchCriteria> searchCriteria, Integer pageNum, Integer pageSize,
                      List<OrderDTO> orderDTOListExpected) {
        OrderDTOMapper orderDTOMapper = new OrderDTOMapperImpl();
        OrdersService ordersService1 = new OrdersService(ordersRepository, orderDTOMapper, moviesRepository, entityService);
        List<OrderEntity> orderEntityList = orderDTOListExpected.stream()
                .map((o) -> {
                    MovieEntity movieEntity = new MovieEntity();
                    movieEntity.setId(o.getMovieId());
                    OrderEntity orderEntity = orderDTOMapper.orderDTOToOrder(o);
                    orderEntity.setMovie(movieEntity);
                    return orderEntity;
                }).collect(Collectors.toList());
        Page<OrderEntity> page = new PageImpl<>(orderEntityList,
                PageRequest.of(pageNum, pageSize), 0);

        when(entityService
                .findEntityPageByFields(searchCriteria, pageNum, pageSize, ordersRepository)).thenReturn(page);

        List<OrderDTO> orderDTOListActual = ordersService1.findByFields(searchCriteria, pageNum, pageSize);

        Assertions.assertEquals(orderDTOListExpected, orderDTOListActual);
    }
}