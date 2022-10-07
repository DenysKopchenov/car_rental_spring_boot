package com.dkop.car.rental.service;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.model.order.OrderStatus;
import com.dkop.car.rental.model.order.RentOrder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    void calculateOrder(OrderDto orderDto);

    void createOrder(OrderDto orderDto);

    List<RentOrder> findOrdersByAppUserId(UUID clientId);

    List<RentOrder> findOrdersOrderStatus(OrderStatus orderStatus);

    List<RentOrder> showOrdersThatExpiresToday(LocalDate now);
}
