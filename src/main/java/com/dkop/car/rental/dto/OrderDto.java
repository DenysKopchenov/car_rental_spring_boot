package com.dkop.car.rental.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class OrderDto {

    private UUID id;
    private CarDto carDto;
    private AppUserDto appUserDto;
    private String passportData;
    private boolean withDriver;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    private long rentalPrice;
}
