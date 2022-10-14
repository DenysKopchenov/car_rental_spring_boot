package com.dkop.car.rental.service.impl;

import com.dkop.car.rental.dto.CarDto;
import com.dkop.car.rental.dto.CarFilterBean;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.model.car.Car;
import com.dkop.car.rental.model.car.CategoryClass;
import com.dkop.car.rental.model.car.Manufacturer;
import com.dkop.car.rental.repository.CarRepository;
import com.dkop.car.rental.service.CarService;
import lombok.SneakyThrows;
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
    public Page<Car> findAll(PaginationAndSortingBean paginationAndSortingBean, CarFilterBean carFilterBean) {
        if (Objects.isNull(paginationAndSortingBean.getSort())) {
            paginationAndSortingBean.setSort("model");
        }
        int currentPage = paginationAndSortingBean.getPage();
        int currentSize = paginationAndSortingBean.getSize();
        String currentSort = paginationAndSortingBean.getSort();
        String direction = paginationAndSortingBean.getDirection();

        List<CategoryClass> categories = carFilterBean.getCategories();
        if (categories.isEmpty()) {
            categories = Arrays.stream(CategoryClass.values()).collect(Collectors.toList());
        }
        List<Manufacturer> manufacturers = carFilterBean.getManufacturers();
        if (manufacturers.isEmpty()) {
            manufacturers = Arrays.stream(Manufacturer.values()).collect(Collectors.toList());
        }
        String model = carFilterBean.getModel();
        long minPrice = carFilterBean.getMinPrice();
        if (minPrice <= 0) {
            minPrice = carRepository.findMinPrice();
            carFilterBean.setMinPrice(minPrice);
        }
        long maxPrice = carFilterBean.getMaxPrice();
        if (maxPrice < minPrice) {
            maxPrice = carRepository.findMaxPrice();
            carFilterBean.setMaxPrice(maxPrice);
        }

        PageRequest of = PageRequest.of(currentPage - 1, currentSize, Sort.by(Sort.Direction.valueOf(direction), currentSort));
        return carRepository.findByManufacturerInAndCategoryClassInAndModelContainsIgnoreCaseAndPricePerDayBetween(manufacturers,
                categories, model, minPrice, maxPrice, of);
    }


    @SneakyThrows
    @Override
    public Car saveCar(CarDto carDto) {
        Car car = new Car();
        car.setCategoryClass(carDto.getCategoryClass());
        car.setManufacturer(carDto.getManufacturer());
        car.setModel(carDto.getModel());
        car.setPricePerDay(carDto.getPricePerDay());
        car.setImage(carDto.getImage().getBytes());
        return carRepository.save(car);
    }

    @Override
    public Car findById(UUID id) {
        return carRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public byte[] findImageByCarId(UUID id) {
        return carRepository.findCarImageByCarId(id);
    }

    @SneakyThrows
    @Override
    public Car updateCar(CarDto carDto) {
        Car car = new Car();
        car.setId(carDto.getId());
        car.setCategoryClass(carDto.getCategoryClass());
        car.setManufacturer(carDto.getManufacturer());
        car.setModel(carDto.getModel());
        car.setPricePerDay(carDto.getPricePerDay());
        car.setImage(carDto.getImage().getBytes());
        return carRepository.save(car);
    }

    @Override
    public void deleteCar(UUID id) {
        carRepository.deleteById(id);
    }
}
