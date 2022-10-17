package com.dkop.car.rental.service.impl;

import com.dkop.car.rental.dto.CarDto;
import com.dkop.car.rental.dto.CarFilterBean;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.model.car.Car;
import com.dkop.car.rental.model.car.CategoryClass;
import com.dkop.car.rental.model.car.Manufacturer;
import com.dkop.car.rental.repository.CarRepository;
import com.dkop.car.rental.service.CarService;
import com.dkop.car.rental.util.Mapper;
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

    private static final String DEFAULT_CAR_SORT = "model";
    private final CarRepository carRepository;
    private final Mapper mapper;

    @Autowired
    public CarServiceImpl(CarRepository carRepository, Mapper mapper) {
        this.carRepository = carRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<Car> findPaginated(Pageable pageable) {
        return carRepository.findAll(pageable);
    }

    @Override
    public Page<Car> findAll(PaginationAndSortingBean pagination, CarFilterBean carFilterBean) {
        if (Objects.isNull(pagination.getSort())) {
            pagination.setSort(DEFAULT_CAR_SORT);
        }
        List<CategoryClass> categories = getCategoryClassListFromFilter(carFilterBean);
        List<Manufacturer> manufacturers = getManufacturersFromFilter(carFilterBean);
        long minPrice = getMinPriceFromFilter(carFilterBean);
        long maxPrice = getMaxPriceFromFilter(carFilterBean, minPrice);
        String model = carFilterBean.getModel();

        PageRequest of = PageRequest.of(pagination.getPage() - 1, pagination.getSize(), Sort.by(Sort.Direction.valueOf(pagination.getDirection()), pagination.getSort()));
        return carRepository.findByManufacturerInAndCategoryClassInAndModelContainsIgnoreCaseAndPricePerDayBetween(manufacturers,
                categories, model, minPrice, maxPrice, of);
    }

    @SneakyThrows
    @Override
    public Car saveCar(CarDto carDto) {
        Car car = mapper.mapCarDtoToCar(carDto);
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
        Car car = mapper.mapCarDtoToCar(carDto);
        if (Objects.isNull(car.getImage())) {
            car.setImage(findImageByCarId(car.getId()));
        }
        return carRepository.save(car);
    }

    @Override
    public void deleteCar(UUID id) {
        carRepository.deleteById(id);
    }

    private long getMaxPriceFromFilter(CarFilterBean carFilterBean, long minPrice) {
        long maxPrice = carFilterBean.getMaxPrice();
        if (maxPrice < minPrice) {
            maxPrice = carRepository.findMaxPrice();
            carFilterBean.setMaxPrice(maxPrice);
        }
        return maxPrice;
    }

    private long getMinPriceFromFilter(CarFilterBean carFilterBean) {
        long minPrice = carFilterBean.getMinPrice();
        if (minPrice <= 0) {
            minPrice = carRepository.findMinPrice();
            carFilterBean.setMinPrice(minPrice);
        }
        return minPrice;
    }

    private static List<CategoryClass> getCategoryClassListFromFilter(CarFilterBean carFilterBean) {
        List<CategoryClass> categories = carFilterBean.getCategories();
        if (categories.isEmpty()) {
            categories = Arrays.stream(CategoryClass.values()).collect(Collectors.toList());
        }
        return categories;
    }

    private static List<Manufacturer> getManufacturersFromFilter(CarFilterBean carFilterBean) {
        List<Manufacturer> manufacturers = carFilterBean.getManufacturers();
        if (manufacturers.isEmpty()) {
            manufacturers = Arrays.stream(Manufacturer.values()).collect(Collectors.toList());
        }
        return manufacturers;
    }
}
