package com.dkop.car.rental.repository;

import com.dkop.car.rental.model.order.OrderStatus;
import com.dkop.car.rental.model.order.RentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<RentOrder, UUID> {

    List<RentOrder> findByOrderDetailsOrderStatus(OrderStatus orderStatus);

    List<RentOrder> findOrdersByAppUserId(UUID appUserId);

    List<RentOrder> findByOrderDetails_EndDateLessThanEqual(LocalDate endDate);
}
