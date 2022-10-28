package com.dkop.car.rental.dto;

import com.dkop.car.rental.model.car.CategoryClass;
import com.dkop.car.rental.model.car.Manufacturer;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CarFilterBean {

    private List<Manufacturer> manufacturers = new ArrayList<>();
    private List<CategoryClass> categories = new ArrayList<>();
    private String model = "";
    private long minPrice;
    private long maxPrice;
}
