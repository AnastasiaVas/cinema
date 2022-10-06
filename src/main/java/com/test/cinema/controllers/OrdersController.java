package com.test.cinema.controllers;

import com.test.cinema.dto.OrderDTO;
import com.test.cinema.dto.checks.CreateChecks;
import com.test.cinema.dto.checks.UpdateChecks;
import com.test.cinema.services.OrdersService;
import com.test.cinema.specification.SearchCriteria;
import com.test.cinema.utils.enums.CreateUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody @Validated(CreateChecks.class) OrderDTO orderDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordersService.createOrUpdateOrder(orderDTO, CreateUpdate.CREATE));
    }

    @PatchMapping
    public ResponseEntity<OrderDTO> updateOrder(@RequestBody @Validated(UpdateChecks.class) OrderDTO orderDTO) {
        return ResponseEntity.ok().body(ordersService.createOrUpdateOrder(orderDTO, CreateUpdate.UPDATE));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Integer orderId) {
        return ResponseEntity.ok().body(ordersService.findOrderById(orderId));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrderById(@PathVariable Integer orderId) {
        return ResponseEntity.ok().body(ordersService.deleteOrderById(orderId));
    }

    @PostMapping("/criteria")
    public ResponseEntity<List<OrderDTO>> findByCriteria(@RequestBody List<SearchCriteria> searchCriteria,
                                                         @RequestParam Integer pageNum,
                                                         @RequestParam Integer pageSize) {
        return ResponseEntity.ok().body(ordersService.findByFields(searchCriteria, pageNum, pageSize));
    }
}
