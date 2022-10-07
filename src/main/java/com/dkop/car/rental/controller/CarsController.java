package com.dkop.car.rental.controller;


import com.dkop.car.rental.dto.CarDto;
import com.dkop.car.rental.model.car.Car;
import com.dkop.car.rental.service.CarService;
import com.dkop.car.rental.util.Mapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cars")
public class CarsController {

    private static final String TITLE_ATTRIBUTE = "title";
    private static final String CARS_TITLE = "Cars";
    private static final String NEW_CAR_TITLE = "Add new car";

    private final CarService carService;
    private final Mapper mapper;

    public CarsController(CarService carService, Mapper mapper) {
        this.carService = carService;
        this.mapper = mapper;
    }

    @GetMapping
    public String showCarList(Model model) {
        model.addAttribute(TITLE_ATTRIBUTE, CARS_TITLE);
        List<CarDto> cars = carService.showCars(1, 2, "1").stream().map(car -> new CarDto(car.getId(),
                car.getManufacturer(),
                car.getCategoryClass(),
                car.getModel(),
                car.getPricePerDay())).collect(Collectors.toList());
        model.addAttribute("cars", cars);
        return "cars/cars";
    }

    @GetMapping("/{id}")
    public String showCar(@PathVariable("id") UUID id, Model model) {
        Car car = carService.findById(id);
        CarDto carDto = mapper.mapCarToCarDto(car);
        model.addAttribute(TITLE_ATTRIBUTE, car.getModel());
        model.addAttribute("car", carDto);
        return "cars/carInfo";
    }
}
