package com.test.cinema.db.repositories;

import com.test.cinema.db.entites.OrderEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends GeneralRepository<OrderEntity, Integer> {
}
