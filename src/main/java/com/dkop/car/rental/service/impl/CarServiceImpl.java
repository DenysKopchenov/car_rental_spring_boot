package com.dkop.car.rental.service.impl;

import com.dkop.car.rental.dto.CarDto;
import com.dkop.car.rental.dto.CarFilterBean;
import com.dkop.car.rental.model.car.Car;
import com.dkop.car.rental.model.car.CategoryClass;
import com.dkop.car.rental.model.car.Manufacturer;
import com.dkop.car.rental.repository.CarRepository;
import com.dkop.car.rental.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    private CarRepository carRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public Page<Car> findPaginated(Pageable pageable) {
        return carRepository.findAll(pageable);
    }

    @Override
    public Page<Car> findAll(Optional<Integer> page, Optional<Integer> size, Optional<String> sort, CarFilterBean carFilterBean) {
        int currentPage = page.orElse(1);
        int currentSize = size.orElse(5);
        String currentSort = sort.orElse("model");
        List<CategoryClass> categories = carFilterBean.getCategories();
        if (Objects.isNull(categories) || categories.isEmpty()) {
            categories = Arrays.stream(CategoryClass.values()).collect(Collectors.toList());
        }
        List<Manufacturer> manufacturers = carFilterBean.getManufacturers();
        if (Objects.isNull(manufacturers) || manufacturers.isEmpty()) {
            manufacturers = Arrays.stream(Manufacturer.values()).collect(Collectors.toList());
        }
        String model = carFilterBean.getModel();
        if (Objects.isNull(model)) {
            model = "";
        }
        Long minPrice = carFilterBean.getMinPrice();
        if (Objects.isNull(minPrice)) {
            minPrice = carRepository.findMinPrice();
        }
        Long maxPrice = carFilterBean.getMaxPrice();
        if (Objects.isNull(maxPrice)){
            maxPrice = carRepository.findMaxPrice();
        }


        PageRequest of = PageRequest.of(currentPage - 1, currentSize, Sort.by(currentSort));
        return carRepository.findByManufacturerInAndCategoryClassInAndModelContainsIgnoreCaseAndPricePerDayBetween(manufacturers,
                categories, model, minPrice, maxPrice, of);
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
