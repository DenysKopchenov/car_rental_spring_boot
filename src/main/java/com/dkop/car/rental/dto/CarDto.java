package com.dkop.car.rental.dto;

import com.dkop.car.rental.model.car.CategoryClass;
import com.dkop.car.rental.model.car.Fuel;
import com.dkop.car.rental.model.car.Manufacturer;
import com.dkop.car.rental.model.car.Transmission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CarDto {

    private UUID id;
    private Manufacturer manufacturer;
    private CategoryClass categoryClass;
    private Fuel fuel;
    private Transmission transmission;
    private String model;
    private long pricePerDay;
    private MultipartFile image;
    private boolean isAvailable;
}
