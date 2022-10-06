package com.test.cinema.utils;

import com.test.cinema.dto.MovieDTO;
import com.test.cinema.dto.OrderDTO;
import com.test.cinema.specification.SearchCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Arguments {

    public static Stream<org.junit.jupiter.params.provider.Arguments> argumentsForFindMovieByCriteria() {
        MovieDTO movieDTO1 = new MovieDTO(100, "Movie 1", 1998,  "Movie 1 description");
        MovieDTO movieDTO2 = new MovieDTO(101, "Movie 2", 2000,  "Movie 2 description");
        MovieDTO movieDTO3 = new MovieDTO(102, "Movie 3", 2009,  "Movie 3 description");

        List<SearchCriteria> searchCriteria1 = new ArrayList<>();
        searchCriteria1.add(new SearchCriteria("name", "=","Movie 1", false));
        List<MovieDTO> movieDTOList1 = new ArrayList<>();
        movieDTOList1.add(movieDTO1);

        List<SearchCriteria> searchCriteria2 = new ArrayList<>();
        searchCriteria2.add(new SearchCriteria("releaseYear", ">",1999, false));
        List<MovieDTO> movieDTOList2 = new ArrayList<>();
        movieDTOList2.add(movieDTO2);
        movieDTOList2.add(movieDTO3);

        List<SearchCriteria> searchCriteria3 = new ArrayList<>();
        searchCriteria3.add(new SearchCriteria("description", "=","Movie 3 description", true));
        searchCriteria3.add(new SearchCriteria("releaseYear", "<",2005, true));
        List<MovieDTO> movieDTOList3 = new ArrayList<>();
        movieDTOList3.add(movieDTO1);
        movieDTOList3.add(movieDTO2);
        movieDTOList3.add(movieDTO3);

        List<MovieDTO> movieDTOList4 = new ArrayList<>();

        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(searchCriteria1, "0", "10", movieDTOList1),
                org.junit.jupiter.params.provider.Arguments.of(searchCriteria2, "0", "10", movieDTOList2),
                org.junit.jupiter.params.provider.Arguments.of(searchCriteria3, "0", "10", movieDTOList3),
                org.junit.jupiter.params.provider.Arguments.of(searchCriteria3, "1", "5", movieDTOList4)
        );
    }

    private static Stream<org.junit.jupiter.params.provider.Arguments> argumentsForFindOrderByCriteria() {
        OrderDTO orderDTO1 = new OrderDTO(100, 100, 3);
        OrderDTO orderDTO2 = new OrderDTO(101, 101, 1);
        OrderDTO orderDTO3 = new OrderDTO(102, 101, 5);

        List<SearchCriteria> searchCriteria1 = new ArrayList<>();
        searchCriteria1.add(new SearchCriteria("id", "=", 101, true));
        searchCriteria1.add(new SearchCriteria("id", "=", 102, true));
        List<OrderDTO> orderDTOList1 = new ArrayList<>();
        orderDTOList1.add(orderDTO2);
        orderDTOList1.add(orderDTO3);

        List<SearchCriteria> searchCriteria2 = new ArrayList<>();
        searchCriteria2.add(new SearchCriteria("ticketQuantity", ">",2, false));
        List<OrderDTO> orderDTOList2 = new ArrayList<>();
        orderDTOList2.add(orderDTO1);
        orderDTOList2.add(orderDTO3);

        List<OrderDTO> orderDTOList3 = new ArrayList<>();

        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(searchCriteria1, "0", "10", orderDTOList1),
                org.junit.jupiter.params.provider.Arguments.of(searchCriteria2, "0", "10", orderDTOList2),
                org.junit.jupiter.params.provider.Arguments.of(searchCriteria2, "1", "5", orderDTOList3)
        );
    }

}
