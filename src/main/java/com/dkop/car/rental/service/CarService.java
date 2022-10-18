package com.dkop.car.rental.service;


import com.dkop.car.rental.dto.CarDto;
import com.dkop.car.rental.dto.CarFilterBean;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.model.car.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CarService {

    Car saveCar(CarDto carDto);

    Car findById(UUID id);

    byte[] findImageByCarId(UUID id);

    Car updateCar(CarDto carDto);

    void deleteCar(UUID id);

    Page<Car> findPaginated(Pageable pageable);

    Page<Car> findAll(PaginationAndSortingBean paginationAndSortingBean, CarFilterBean carFilterBean);

    Page<Car> findAllAvailable(PaginationAndSortingBean paginationAndSortingBean, CarFilterBean carFilterBean);

    void setAvailability(UUID id, Boolean available);
}
