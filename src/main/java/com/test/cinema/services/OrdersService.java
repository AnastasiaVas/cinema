package com.test.cinema.services;

import com.test.cinema.db.entites.OrderEntity;
import com.test.cinema.dto.OrderDTO;
import com.test.cinema.mapper.OrderDTOMapper;
import com.test.cinema.db.repositories.MoviesRepository;
import com.test.cinema.db.repositories.OrdersRepository;
import com.test.cinema.specification.SearchCriteria;
import com.test.cinema.utils.CustomMessageGenerator;
import com.test.cinema.utils.enums.CreateUpdate;
import com.test.cinema.utils.enums.Entities;
import com.test.cinema.utils.enums.EntityFields;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.nonNull;

@Service
@AllArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final OrderDTOMapper orderDTOMapper;
    private final MoviesRepository moviesRepository;
    private final EntityService entityService;

    public List<OrderDTO> findByFields(List<SearchCriteria> searchCriteria, int pageNum, int pageSize) {
        Page<OrderEntity> orders = entityService
                .findEntityPageByFields(searchCriteria, pageNum, pageSize, ordersRepository);
        return orders.stream()
                .map(orderDTOMapper::orderEntityToOrderDTO)
                .toList();
    }

    public OrderDTO createOrUpdateOrder(OrderDTO orderDTO, CreateUpdate createUpdate) {
        OrderEntity orderEntity = getOrderForCreateOrUpdate(createUpdate, orderDTO.getId());
        OrderEntity orderEntityToSave = orderDTOMapper.orderDTOToOrder(orderDTO, orderEntity);

        if (nonNull(orderDTO.getMovieId())) {
           orderEntityToSave.setMovie(entityService
                    .getEntityByIdIfPresentOrThrowException(orderDTO.getMovieId(), moviesRepository, Entities.MOVIE));
        }

        return orderDTOMapper.orderEntityToOrderDTO(ordersRepository.save(orderEntityToSave));
    }

    public String deleteOrderById(Integer orderId) {
        OrderEntity orderEntity = entityService.getEntityByIdIfPresentOrThrowException(orderId,
                ordersRepository, Entities.ORDER);
        ordersRepository.delete(orderEntity);
        return CustomMessageGenerator.entityDeleted(Entities.ORDER, EntityFields.ID, String.valueOf(orderId));
    }

    public OrderDTO findOrderById(Integer orderId) {
        return orderDTOMapper.orderEntityToOrderDTO(entityService
                .getEntityByIdIfPresentOrThrowException(orderId, ordersRepository, Entities.ORDER));
    }

    private OrderEntity getOrderForCreateOrUpdate(CreateUpdate createUpdate, Integer orderId) {
        if (createUpdate == CreateUpdate.UPDATE) {
            return entityService.getEntityByIdIfPresentOrThrowException(orderId,
                    ordersRepository, Entities.ORDER);
        }
        return new OrderEntity();
    }
}
