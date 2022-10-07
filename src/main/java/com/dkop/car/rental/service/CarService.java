package com.dkop.car.rental.service;


import com.dkop.car.rental.dto.CarDto;
import com.dkop.car.rental.model.car.Car;

import java.util.List;
import java.util.UUID;

public interface CarService {

    List<Car> showCars(int page, int perPage, String sortBy);

    Car saveCar(CarDto carDto);

    Car findById(UUID id);

    Car updateCar(CarDto carDto);

    void deleteCar(UUID id);
}
