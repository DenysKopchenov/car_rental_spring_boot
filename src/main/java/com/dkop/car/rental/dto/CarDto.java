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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CarDto {

    private UUID id;
    @NotNull
    private Manufacturer manufacturer;
    @NotNull
    private CategoryClass categoryClass;
    @NotNull
    private Fuel fuel;
    @NotNull
    private Transmission transmission;
    @NotBlank
    private String model;
    @NotNull
    private long pricePerDay;
    private MultipartFile image;
    @NotNull
    private boolean isAvailable;
}
