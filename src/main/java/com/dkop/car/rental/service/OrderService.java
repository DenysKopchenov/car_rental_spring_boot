package com.dkop.car.rental.service;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.dto.OrderFilterBean;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.model.order.RentOrder;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface OrderService {

    void calculateOrder(OrderDto orderDto);

    RentOrder saveOrder(OrderDto orderDto);

    Page<RentOrder> findOrdersByAppUserId(UUID appUserId, PaginationAndSortingBean paginationAndSortingBean, OrderFilterBean orderFilterBean);

    Page<RentOrder> findAllOrders(PaginationAndSortingBean paginationAndSortingBean, OrderFilterBean orderFilterBean);

    RentOrder payOrder(UUID orderId);

    RentOrder findById(UUID orderId);
}
