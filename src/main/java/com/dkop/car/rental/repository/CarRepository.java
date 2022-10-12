package com.dkop.car.rental.repository;

import com.dkop.car.rental.model.car.Car;
import com.dkop.car.rental.model.car.CategoryClass;
import com.dkop.car.rental.model.car.Manufacturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {

    @Query("select c from Car c " +
            "where c.manufacturer in :manufacturers and c.categoryClass in :categoryClasses and upper(c.model) like upper(concat('%', :model, '%')) and c.pricePerDay between :pricePerDayStart and :pricePerDayEnd")
    Page<Car> findByManufacturerInAndCategoryClassInAndModelContainsIgnoreCaseAndPricePerDayBetween(@Param("manufacturers") Collection<Manufacturer> manufacturers, @Param("categoryClasses") Collection<CategoryClass> categoryClasses, @Param("model") String model, @Param("pricePerDayStart") long pricePerDayStart, @Param("pricePerDayEnd") long pricePerDayEnd, Pageable pageable);

    @Query("SELECT min(pricePerDay) FROM Car")
    long findMinPrice();

    @Query("SELECT max(pricePerDay) FROM Car")
    long findMaxPrice();

    @Query("SELECT c.image FROM Car c WHERE c.id= :carId")
    byte[] findCarImageByCarId(@Param("carId") UUID carId);
}
