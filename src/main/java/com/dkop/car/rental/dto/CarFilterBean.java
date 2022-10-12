package com.dkop.car.rental.dto;

import com.dkop.car.rental.model.car.CategoryClass;
import com.dkop.car.rental.model.car.Manufacturer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CarFilterBean {

    private List<Manufacturer> manufacturers;
    private List<CategoryClass> categories;
    private String model;
    private Long minPrice;
    private Long maxPrice;
}
