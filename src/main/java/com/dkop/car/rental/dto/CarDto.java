package com.dkop.car.rental.dto;

import com.dkop.car.rental.model.car.CategoryClass;
import com.dkop.car.rental.model.car.Manufacturer;
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
    private String model;
    private long pricePerDay;
    private MultipartFile image;
}
