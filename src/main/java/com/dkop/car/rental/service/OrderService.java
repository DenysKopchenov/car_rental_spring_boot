package com.dkop.car.rental.service;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.model.order.OrderStatus;
import com.dkop.car.rental.model.order.RentOrder;
import com.dkop.car.rental.model.order.RepairPayment;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    void calculateOrder(OrderDto orderDto);

    RentOrder saveOrder(OrderDto orderDto);

    Page<RentOrder> findOrdersByAppUserId(UUID appUserId, PaginationAndSortingBean paginationAndSortingBean);

    Page<RentOrder> findAllOrders(PaginationAndSortingBean paginationAndSortingBean, List<OrderStatus> statuses);

    RentOrder findById(UUID orderId);

    RentOrder payOrder(UUID orderId);

    RentOrder acceptOrder(UUID orderId);

    RentOrder rejectOrder(UUID orderId, String rejectDetails);

    RentOrder payRepair(UUID orderId);

    RentOrder returnOrderWithoutDamage(UUID orderId);

    RentOrder returnOrderWithDamage(UUID orderId, RepairPayment repairPayment);

    RentOrder completeOrderWithPaidRepair(UUID orderId);

    RentOrder askForReturn(UUID orderId);
}
