package com.dkop.car.rental.util;

import com.dkop.car.rental.dto.AppUserDto;
import com.dkop.car.rental.dto.CarDto;
import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.dto.RegFormDto;
import com.dkop.car.rental.model.car.Car;
import com.dkop.car.rental.model.order.OrderDetails;
import com.dkop.car.rental.model.order.RentOrder;
import com.dkop.car.rental.model.user.AppUser;
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

    public AppUser mapRegFormDtoToUser(RegFormDto regFormDto) {
        AppUser appUser = new AppUser();
        appUser.setFirstName(regFormDto.getFirstName());
        appUser.setLastName(regFormDto.getLastName());
        appUser.setEmail(regFormDto.getEmail());
        appUser.setActive(Boolean.TRUE);
        return appUser;
    }

    public AppUserDto mapAppUserToAppUserDto(AppUser appUser) {
        AppUserDto appUserDto = new AppUserDto();
        appUserDto.setId(appUser.getId());
        appUserDto.setFirstName(appUser.getFirstName());
        appUserDto.setLastName(appUser.getLastName());
        appUserDto.setEmail(appUser.getEmail());
        appUserDto.setActive(appUser.isActive());
        return appUserDto;
    }

    public OrderDetails mapOrderDtoToOrderDetails(OrderDto orderDto) {
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrderStatus(orderDto.getOrderStatus());
        orderDetails.setWithDriver(orderDto.isWithDriver());
        orderDetails.setPassportData(orderDto.getPassportData());
        orderDetails.setEndDate(orderDto.getEndDate());
        orderDetails.setStartDate(orderDto.getStartDate());
        orderDetails.setRentalPrice(orderDto.getRentalPrice());
        orderDetails.setPassportData(orderDto.getPassportData());
        return orderDetails;
    }

    public OrderDto mapRentOrderToOrderDto(RentOrder rentOrder) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(rentOrder.getId());
        orderDto.setCar(rentOrder.getCar());
        orderDto.setAppUserDto(mapAppUserToAppUserDto(rentOrder.getAppUser()));
        OrderDetails orderDetails = rentOrder.getOrderDetails();
        orderDto.setWithDriver(orderDetails.isWithDriver());
        orderDto.setStartDate(orderDetails.getStartDate());
        orderDto.setEndDate(orderDetails.getEndDate());
        orderDto.setRentalPrice(orderDetails.getRentalPrice());
        orderDto.setOrderStatus(orderDetails.getOrderStatus());
        orderDto.setRepairPayment(orderDetails.getRepairPayment());
        orderDto.setRejectOrderDetails(orderDetails.getRejectOrderDetails());
        orderDto.setRepairPayment(orderDetails.getRepairPayment());
        return orderDto;
    }
}
