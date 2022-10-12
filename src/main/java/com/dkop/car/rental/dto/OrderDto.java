package com.dkop.car.rental.dto;

import com.dkop.car.rental.model.car.Car;
import com.dkop.car.rental.model.client.AppUser;
import com.dkop.car.rental.model.order.OrderStatus;
import com.dkop.car.rental.model.order.PassportData;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class OrderDto {

    private UUID id;
    private Car car;
    private AppUser appUser;
    private PassportData passportData;
    private boolean withDriver;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    private long rentalPrice;
    private OrderStatus orderStatus;
    private String rejectOrderDetails;
}
