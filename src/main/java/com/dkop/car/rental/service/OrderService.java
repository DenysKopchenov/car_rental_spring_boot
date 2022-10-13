package com.dkop.car.rental.service;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.dto.OrderFilterBean;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.model.order.OrderStatus;
import com.dkop.car.rental.model.order.RentOrder;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    void calculateOrder(OrderDto orderDto);

    void createOrder(OrderDto orderDto);

    Page<RentOrder> findPagedOrdersByAppUserId(UUID clientId, PaginationAndSortingBean paginationAndSortingBean, OrderFilterBean orderFilterBean);

    List<RentOrder> findOrdersOrderStatus(OrderStatus orderStatus);

    List<RentOrder> showOrdersThatExpiresToday(LocalDate now);
}
