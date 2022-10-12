package com.dkop.car.rental.dto;

import com.dkop.car.rental.model.car.CategoryClass;
import com.dkop.car.rental.model.car.Manufacturer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CarFilterBean {

    private List<Manufacturer> manufacturers;
    private List<CategoryClass> categories;
    private String model;
    @Digits(integer = 1, fraction = 0)
    private Long minPrice;
    @Digits(integer = 1, fraction = 0)
    private Long maxPrice;
}
