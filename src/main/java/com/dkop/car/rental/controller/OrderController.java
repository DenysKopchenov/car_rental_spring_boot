package com.dkop.car.rental.controller;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.model.car.Car;
import com.dkop.car.rental.service.CarService;
import com.dkop.car.rental.service.OrderService;
import com.dkop.car.rental.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/order")
@PreAuthorize("hasAuthority('USER')")
public class OrderController {

    private final CarService carService;
    private final OrderService orderService;
    private final Mapper mapper;

    @Autowired
    public OrderController(CarService carService, OrderService orderService, Mapper mapper) {
        this.carService = carService;
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @GetMapping()
    public String showOrderForm(@RequestParam("carId") UUID carId, @ModelAttribute("order") OrderDto orderDto) {
        Car car = carService.findById(carId);
        orderDto.setCar(car);
        orderDto.setStartDate(LocalDate.now());
        return "orders/calculateForm";
    }

    @GetMapping("/book")
    public String showPayment(@ModelAttribute("order") OrderDto orderDto) {
        orderService.calculateOrder(orderDto);
        return "orders/orderForm";
    }

    @PostMapping("/book")
    public String payPayment(@ModelAttribute("order") OrderDto orderDto) {
        orderService.createOrder(orderDto);
        return "orders/orderForm";
    }
}
