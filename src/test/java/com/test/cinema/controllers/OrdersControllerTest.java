package com.test.cinema.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.cinema.dto.MovieDTO;
import com.test.cinema.dto.OrderDTO;
import com.test.cinema.exception.IncorrectlyFilledFieldsExceptionMessage;
import com.test.cinema.specification.SearchCriteria;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class OrdersControllerTest {

    @Container
    public static PostgreSQLContainer container = new PostgreSQLContainer("postgres:13.4-buster");


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    private static final String EXCEPTION_MESSAGE = "Please, input correct values.";

    @BeforeAll
    public static void setUp(){
        container.withReuse(true);
        container.start();
    }

    @AfterAll
    public static void tearDown() {
        container.stop();
    }

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.driver-class-name", container::getDriverClassName);
    }

    private static Stream<Arguments> argumentsForCreateOrderInvalidRequest() {

        OrderDTO orderDTO1 = new OrderDTO();
        orderDTO1.setId(1);
        IncorrectlyFilledFieldsExceptionMessage exMessage1 = new IncorrectlyFilledFieldsExceptionMessage();
        exMessage1.setExceptionMessage(EXCEPTION_MESSAGE);
        Map<String, String > map1 = new HashMap<>();
        map1.put("id", "id shouldn't be filled on create");
        exMessage1.setIncorrectFilledFieldsMessages(map1);

        OrderDTO orderDTO2 = new OrderDTO();
        IncorrectlyFilledFieldsExceptionMessage exMessage2 = new IncorrectlyFilledFieldsExceptionMessage();
        exMessage2.setExceptionMessage(EXCEPTION_MESSAGE);
        Map<String, String > map2 = new HashMap<>();
        map2.put("movieId", "movieId should be filled on create");
        map2.put("ticketQuantity", "ticketQuantity should be filled on create");
        exMessage2.setIncorrectFilledFieldsMessages(map2);

        OrderDTO orderDTO3 = new OrderDTO();
        orderDTO3.setId(1);
        orderDTO3.setTicketQuantity(0);
        IncorrectlyFilledFieldsExceptionMessage exMessage3 = new IncorrectlyFilledFieldsExceptionMessage();
        exMessage3.setExceptionMessage(EXCEPTION_MESSAGE);
        Map<String, String > map3 = new HashMap<>();
        map3.put("movieId", "movieId should be filled on create");
        map3.put("ticketQuantity", "ticket quantity should be a at least 1");
        exMessage3.setIncorrectFilledFieldsMessages(map3);

        return Stream.of(
                Arguments.of(orderDTO1, exMessage1),
                Arguments.of(orderDTO2, exMessage2),
                Arguments.of(orderDTO3, exMessage3)
        );
    }

    @Test
    void createOrder_SUCCESS() throws Exception {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setMovieId(100);
        orderDTO.setTicketQuantity(2);
        orderDTO.setOrderNumber(3456789063L);

        mockMvc.perform(post("/order")
                        .content(objectMapper.writeValueAsString(orderDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.movieId").value(orderDTO.getMovieId()))
                .andExpect(jsonPath("$.ticketQuantity").value(orderDTO.getTicketQuantity()))
                .andExpect(jsonPath("$.orderNumber").value(orderDTO.getOrderNumber()))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource("argumentsForCreateOrderInvalidRequest")
    void createOrder_INVALID_REQUEST(OrderDTO orderDTO,
                         IncorrectlyFilledFieldsExceptionMessage incorrectlyFilledFieldsExceptionMessage) throws Exception {
        mockMvc.perform(post("/order")
                        .content(objectMapper.writeValueAsString(orderDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(incorrectlyFilledFieldsExceptionMessage)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateOrder_SUCCESS() throws Exception {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(101);
        orderDTO.setMovieId(102);
        orderDTO.setTicketQuantity(10);
        String jsonOrderDTO = objectMapper.writeValueAsString(orderDTO);

        mockMvc.perform(patch("/order")
                        .content(jsonOrderDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.id").value(orderDTO.getId()))
                .andExpect(jsonPath("$.movieId").value(orderDTO.getMovieId()))
                .andExpect(jsonPath("$.ticketQuantity").value(orderDTO.getTicketQuantity()))
                .andExpect(status().isOk());
    }

    @Test
    void updateOrder_MOVIE_NOT_FOUND() throws Exception {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(101);
        orderDTO.setMovieId(110);
        String jsonOrderDTO = objectMapper.writeValueAsString(orderDTO);

        mockMvc.perform(patch("/order")
                        .content(jsonOrderDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Movie with id 110 doesn't exist."))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOrder_INVALID_REQUEST() throws Exception {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setTicketQuantity(6);
        IncorrectlyFilledFieldsExceptionMessage incorrectlyFilledFieldsMessage
                = new IncorrectlyFilledFieldsExceptionMessage();
        incorrectlyFilledFieldsMessage.setExceptionMessage(EXCEPTION_MESSAGE);
        Map<String, String > map = new HashMap<>();
        map.put("id", "id should be filled on update");
        incorrectlyFilledFieldsMessage.setIncorrectFilledFieldsMessages(map);

        mockMvc.perform(patch("/order")
                        .content(objectMapper.writeValueAsString(orderDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(incorrectlyFilledFieldsMessage)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getOrderById_SUCCESS() throws Exception {
        mockMvc.perform(get("/order/{orderId}",100 ))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.movieId").value(100))
                .andExpect(jsonPath("$.ticketQuantity").value(3))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderById_EXCEPTION_ID_INVALID() throws Exception {
        mockMvc.perform(get("/order/{orderId}","-1" ))
                .andDo(print())
                .andExpect(content().string("Id should be a positive digit."))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrderById_NOT_FOUND() throws Exception {
        int id = 200;
        mockMvc.perform(get("/order/{orderId}",id ))
                .andExpect(content().string("Order with id 200 doesn't exist."))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteOrderById_SUCCESS() throws Exception {
        int id = 102;

        mockMvc.perform(delete("/order/{orderId}",id ))
                .andExpect(content().string("Order with id 102 was successfully deleted."))
                .andExpect(status().isOk());
    }

    @Test
    void deleteOrderById_INVALID_ID() throws Exception {
        mockMvc.perform(delete("/order/{orderId}","0" ))
                .andDo(print())
                .andExpect(content().string("Id should be a positive digit."))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteOrderById_NOT_FOUND() throws Exception {
        int id = 200;
        mockMvc.perform(delete("/order/{orderId}",id ))
                .andExpect(content().string("Order with id 200 doesn't exist."))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("com.test.cinema.utils.Arguments#argumentsForFindOrderByCriteria")
    void findByCriteria(List<SearchCriteria> searchCriteriaList, String pageNumber,
                        String pageSize, List<MovieDTO> orderDTOList) throws Exception {
        mockMvc.perform(post("/order/criteria")
                        .param("pageNum", pageNumber)
                        .param("pageSize", pageSize)
                        .content(objectMapper.writeValueAsString(searchCriteriaList))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(orderDTOList)))
                .andExpect(status().isOk());
    }
}