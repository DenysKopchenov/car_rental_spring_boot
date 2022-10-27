package com.dkop.car.rental.repository;

import com.dkop.car.rental.model.order.OrderStatus;
import com.dkop.car.rental.model.order.RentOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<RentOrder, UUID> {

    @Query("select (count(r) > 0) from RentOrder r " +
            "where r.car.id = :carId and r.appUser.id = :userId and r.orderDetails.startDate = :startDate and r.orderDetails.endDate = :endDate and r.orderDetails.withDriver = :withDriver and r.orderDetails.orderStatus = :orderStatus")
    boolean isOrderExist(@Param("carId") UUID carId,
                         @Param("userId") UUID userId,
                         @Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate,
                         @Param("withDriver") boolean withDriver,
                         @Param("orderStatus") OrderStatus orderStatus);


    Page<RentOrder> findOrdersByAppUserId(UUID appUserId, Pageable pageable);

    @Query("select r from RentOrder r " +
            "where r.orderDetails.orderStatus in :statuses")
    Page<RentOrder> findAllFilteredByStatuses(@Param("statuses") List<OrderStatus> statuses, Pageable pageable);
}
