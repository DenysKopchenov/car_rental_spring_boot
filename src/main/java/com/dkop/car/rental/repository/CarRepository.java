package com.dkop.car.rental.repository;

import com.dkop.car.rental.model.car.Car;
import com.dkop.car.rental.model.car.CategoryClass;
import com.dkop.car.rental.model.car.Manufacturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {

    Page<Car> findByManufacturerInAndCategoryClassInAndModelContainsIgnoreCaseAndPricePerDayBetween(Collection<Manufacturer> manufacturers, Collection<CategoryClass> categoryClasses, String model, long pricePerDayStart, long pricePerDayEnd, Pageable pageable);

    @Query("SELECT min(pricePerDay) FROM Car")
    long findMinPrice();

    @Query("SELECT max(pricePerDay) FROM Car")
    long findMaxPrice();
}
