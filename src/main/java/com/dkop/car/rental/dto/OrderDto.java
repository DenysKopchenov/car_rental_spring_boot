package com.dkop.car.rental.dto;

import com.dkop.car.rental.model.car.Car;
import com.dkop.car.rental.model.order.OrderStatus;
import com.dkop.car.rental.model.order.PassportData;
import com.dkop.car.rental.model.order.RepairPayment;
import com.dkop.car.rental.model.user.AppUser;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class OrderDto {

    private UUID id;
    private Car car;
    private AppUser appUser;
    @Valid
    private PassportData passportData;
    private boolean withDriver;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    private long rentalPrice;
    private OrderStatus orderStatus;
    private RepairPayment repairPayment;
}
