package com.dkop.car.rental.util;

import com.dkop.car.rental.dto.CarDto;
import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.dto.RegFormDto;
import com.dkop.car.rental.model.car.Car;
import com.dkop.car.rental.model.client.AppUser;
import com.dkop.car.rental.model.order.OrderDetails;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public Car mapCarDtoToCar(CarDto carDto) {
        Car car = new Car();
        car.setId(carDto.getId());
        car.setModel(carDto.getModel());
        car.setManufacturer(carDto.getManufacturer());
        car.setCategoryClass(carDto.getCategoryClass());
        car.setPricePerDay(carDto.getPricePerDay());
        return car;
    }

    public CarDto mapCarToCarDto(Car car) {
        CarDto carDto = new CarDto();
        carDto.setId(car.getId());
        carDto.setManufacturer(car.getManufacturer());
        carDto.setCategoryClass(car.getCategoryClass());
        carDto.setModel(car.getModel());
        carDto.setPricePerDay(car.getPricePerDay());
        return carDto;
    }

    public AppUser mapUserDtoToUser(RegFormDto regFormDto) {
        AppUser appUser = new AppUser();
        appUser.setFirstName(regFormDto.getFirstName());
        appUser.setLastName(regFormDto.getLastName());
        appUser.setEmail(regFormDto.getEmail());
        appUser.setActive(Boolean.TRUE);
        return appUser;
    }

    public OrderDetails mapOrderDtoToOrderDetails(OrderDto orderDto) {
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrderStatus(orderDetails.getOrderStatus());
        orderDetails.setWithDriver(orderDetails.isWithDriver());
        orderDetails.setPassportData(orderDto.getPassportData());
        orderDetails.setEndDate(orderDto.getEndDate());
        orderDetails.setStartDate(orderDto.getStartDate());
        orderDetails.setRentalPrice(orderDto.getRentalPrice());
        return orderDetails;
    }
}
