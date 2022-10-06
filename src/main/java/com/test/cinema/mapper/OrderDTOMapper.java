package com.test.cinema.mapper;

import com.test.cinema.db.entites.OrderEntity;
import com.test.cinema.dto.OrderDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrderDTOMapper {

    @Mapping(target = "movieId", source = "movie.id")
    OrderDTO orderEntityToOrderDTO(OrderEntity orderEntity);

    OrderEntity orderDTOToOrder(OrderDTO orderDTO);

    @BeanMapping(nullValuePropertyMappingStrategy =  NullValuePropertyMappingStrategy.IGNORE)
    OrderEntity orderDTOToOrder(OrderDTO orderDTO, @MappingTarget OrderEntity orderEntity);

}