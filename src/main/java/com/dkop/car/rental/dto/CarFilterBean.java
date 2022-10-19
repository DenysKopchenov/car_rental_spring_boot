package com.dkop.car.rental.dto;

import com.dkop.car.rental.model.car.CategoryClass;
import com.dkop.car.rental.model.car.Manufacturer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarFilterBean {

    private List<Manufacturer> manufacturers = Collections.emptyList();
    private List<CategoryClass> categories = Collections.emptyList();
    private String model = "";
    private long minPrice;
    private long maxPrice;
}
