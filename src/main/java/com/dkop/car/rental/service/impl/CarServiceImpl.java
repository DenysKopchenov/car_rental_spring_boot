package com.dkop.car.rental.service.impl;

import com.dkop.car.rental.dto.CarDto;
import com.dkop.car.rental.model.car.Car;
import com.dkop.car.rental.repository.CarRepository;
import com.dkop.car.rental.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
public class CarServiceImpl implements CarService {

    private CarRepository carRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public List<Car> showCars(int page, int perPage, String sortBy) {
//        PageRequest pageRequest = PageRequest.of(page, page, Sort.by(sortBy));
//        carRepository.findAll(pageRequest);
        return carRepository.findAll();
    }

    @Override
    public Car saveCar(CarDto carDto) {
        Car car = new Car();
        car.setCategoryClass(carDto.getCategoryClass());
        car.setManufacturer(carDto.getManufacturer());
        car.setModel(carDto.getModel());
        car.setPricePerDay(carDto.getPricePerDay());
        carRepository.save(car);
        return carRepository.save(car);
    }

    @Override
    public Car findById(UUID id) {
        return carRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Car updateCar(CarDto carDto) {
        Car car = new Car();
        car.setId(carDto.getId());
        car.setCategoryClass(carDto.getCategoryClass());
        car.setManufacturer(carDto.getManufacturer());
        car.setModel(carDto.getModel());
        car.setPricePerDay(carDto.getPricePerDay());
        return carRepository.save(car);
    }

    @Override
    public void deleteCar(UUID id) {
        carRepository.deleteById(id);
    }
}
