package com.test.cinema.controllers;

import com.test.cinema.dto.MovieDTO;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class MoviesControllerTest {

    @Container
    public static PostgreSQLContainer container = new PostgreSQLContainer("postgres:11.1");

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

    private static Stream<Arguments> argumentsForCreateMovieInvalidRequest() {

        MovieDTO movieDTO1 = new MovieDTO();
        movieDTO1.setId(1);
        IncorrectlyFilledFieldsExceptionMessage exMessage1 = new IncorrectlyFilledFieldsExceptionMessage();
        exMessage1.setExceptionMessage(EXCEPTION_MESSAGE);
        Map<String, String > map1 = new HashMap<>();
        map1.put("id", "id shouldn't be filled on create");
        exMessage1.setIncorrectFilledFieldsMessages(map1);

        MovieDTO movieDTO2 = new MovieDTO();
        IncorrectlyFilledFieldsExceptionMessage exMessage2 = new IncorrectlyFilledFieldsExceptionMessage();
        exMessage2.setExceptionMessage(EXCEPTION_MESSAGE);
        Map<String, String > map2 = new HashMap<>();
        map2.put("name", "name should be filled on create");
        map2.put("releaseYear", "release year should be filled on create");
        exMessage2.setIncorrectFilledFieldsMessages(map2);

        MovieDTO movieDTO3 = new MovieDTO();
        movieDTO3.setId(1);
        IncorrectlyFilledFieldsExceptionMessage exMessage3 = new IncorrectlyFilledFieldsExceptionMessage();
        exMessage3.setExceptionMessage(EXCEPTION_MESSAGE);
        Map<String, String > map3 = new HashMap<>();
        map3.put("id", "id shouldn't be filled on create");
        map3.put("name", "name should be filled on create");
        map3.put("releaseYear", "release year should be filled on create");
        exMessage3.setIncorrectFilledFieldsMessages(map3);

        return Stream.of(
                Arguments.of(movieDTO1, exMessage1),
                Arguments.of(movieDTO2, exMessage2),
                Arguments.of(movieDTO3, exMessage3)
        );
    }

    @Test
    void createMovie_SUCCESS() throws Exception {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setName("Movie 5");
        movieDTO.setDescription("Movie 5 description");
        movieDTO.setReleaseYear(1999);

        mockMvc.perform(post("/movie")
                .content(objectMapper.writeValueAsString(movieDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(movieDTO.getName()))
                .andExpect(jsonPath("$.releaseYear").value(movieDTO.getReleaseYear()))
                .andExpect(jsonPath("$.description").value(movieDTO.getDescription()))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource("argumentsForCreateMovieInvalidRequest")
    void createMovie_INVALID_REQUEST(MovieDTO movieDTO,
                IncorrectlyFilledFieldsExceptionMessage incorrectlyFilledFieldsExceptionMessage) throws Exception {
        mockMvc.perform(post("/movie")
                .content(objectMapper.writeValueAsString(movieDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(incorrectlyFilledFieldsExceptionMessage)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateMovie_SUCCESS() throws Exception {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setId(101);
        movieDTO.setName("Movie 100");
        movieDTO.setDescription("Movie 1 description");
        String jsonMovieDTO = objectMapper.writeValueAsString(movieDTO);

        mockMvc.perform(patch("/movie")
                        .content(jsonMovieDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(movieDTO.getId()))
                .andExpect(jsonPath("$.name").value(movieDTO.getName()))
                .andExpect(jsonPath("$.description").value(movieDTO.getDescription()))
                .andExpect(status().isOk());
    }

    @Test
    void updateMovie_INVALID_REQUEST() throws Exception {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setName("Movie 100");
        IncorrectlyFilledFieldsExceptionMessage incorrectlyFilledFieldsMessage
                = new IncorrectlyFilledFieldsExceptionMessage();
        incorrectlyFilledFieldsMessage.setExceptionMessage(EXCEPTION_MESSAGE);
        Map<String, String > map = new HashMap<>();
        map.put("id", "id should be filled on update");
        incorrectlyFilledFieldsMessage.setIncorrectFilledFieldsMessages(map);

        mockMvc.perform(patch("/movie")
                        .content(objectMapper.writeValueAsString(movieDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(incorrectlyFilledFieldsMessage)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getMovieById_SUCCESS() throws Exception {
        mockMvc.perform(get("/movie/{movieId}","100" ))
                .andExpect(jsonPath("$.id").value("100"))
                .andExpect(jsonPath("$.name").value("Movie 1"))
                .andExpect(jsonPath("$.releaseYear").value(1998))
                .andExpect(jsonPath("$.description").value("Movie 1 description"))
                .andExpect(status().isOk());
    }

    @Test
    void getMovieById_EXCEPTION_ID_INVALID() throws Exception {
        mockMvc.perform(get("/movie/{movieId}","-1" ))
                .andDo(print())
                .andExpect(content().string("Id should be a positive digit."))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMovieById_NOT_FOUND() throws Exception {
        int id = 200;
        mockMvc.perform(get("/movie/{movieId}",id ))
                .andExpect(content().string("Movie with id 200 doesn't exist."))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMovieById_SUCCESS() throws Exception {
        int id = 102;

        mockMvc.perform(delete("/movie/{movieId}",id ))
                .andExpect(content().string("Movie with id 102 was successfully deleted."))
                .andExpect(status().isOk());
    }

    @Test
    void deleteMovieById_INVALID_ID() throws Exception {
        mockMvc.perform(delete("/movie/{movieId}","0" ))
                .andDo(print())
                .andExpect(content().string("Id should be a positive digit."))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteMovieById_NOT_FOUND() throws Exception {
        int id = 200;
        mockMvc.perform(delete("/movie/{movieId}",id ))
                .andExpect(content().string("Movie with id 200 doesn't exist."))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("com.test.cinema.utils.Arguments#argumentsForFindMovieByCriteria")
    void findByCriteria(List<SearchCriteria> searchCriteriaList, String pageNumber,
                        String pageSize, List<MovieDTO> movieDTOList) throws Exception {
        mockMvc.perform(post("/movie/criteria")
                        .param("pageNum", pageNumber)
                        .param("pageSize", pageSize)
                        .content(objectMapper.writeValueAsString(searchCriteriaList))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(movieDTOList)))
                .andExpect(status().isOk());
    }
}