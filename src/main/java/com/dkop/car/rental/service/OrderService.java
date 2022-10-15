package com.dkop.car.rental.service;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.dto.OrderFilterBean;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.model.order.RentOrder;
import com.dkop.car.rental.model.order.RepairPayment;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface OrderService {

    void calculateOrder(OrderDto orderDto);

    RentOrder saveOrder(OrderDto orderDto);

    Page<RentOrder> findOrdersByAppUserId(UUID appUserId, PaginationAndSortingBean paginationAndSortingBean, OrderFilterBean orderFilterBean);

    Page<RentOrder> findAllOrders(PaginationAndSortingBean paginationAndSortingBean, OrderFilterBean orderFilterBean);

    RentOrder findById(UUID orderId);

//    RentOrder changeOrderStatus(UUID orderId, OrderStatus oldStatus, OrderStatus newStatus);

    RentOrder payOrder(UUID orderId);

    RentOrder acceptOrder(UUID orderId);

    RentOrder rejectOrder(UUID orderId, String rejectDetails);

    RentOrder payRepair(UUID orderId);

    RentOrder returnOrder(UUID orderId, RepairPayment repairPayment);
}
