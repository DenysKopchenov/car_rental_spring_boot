package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.model.car.Car;
import com.dkop.car.rental.model.order.RentOrder;
import com.dkop.car.rental.service.CarService;
import com.dkop.car.rental.service.OrderService;
import com.dkop.car.rental.util.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/order")
@Slf4j
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
    public String showCalculateForm(@RequestParam("carId") UUID carId, @ModelAttribute("order") OrderDto orderDto) {
        Car car = carService.findById(carId);
        orderDto.setCar(car);
        orderDto.setStartDate(LocalDate.now());
        return "orders/calculateForm";
    }

    @GetMapping("/booking")
    public String showPayment(@ModelAttribute("order") OrderDto orderDto, BindingResult bindingResult) {
        LocalDate startDate = orderDto.getStartDate();
        LocalDate endDate = orderDto.getEndDate();
        if (startDate.isAfter(endDate) || startDate.isEqual(endDate)) {
            bindingResult.rejectValue("startDate", "start-date");
            return "orders/calculateForm";
        }
        orderService.calculateOrder(orderDto);
        return "user/bookCarForm";
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showOrder(@PathVariable("id") UUID orderId, Model model) {
        RentOrder order = orderService.findById(orderId);
        OrderDto orderDto = mapper.mapRentOrderToOrderDto(order);
        model.addAttribute("order", orderDto);
        return "orders/orderInfo";
    }

    @ExceptionHandler(RuntimeException.class)
    public void handleRuntimeException(RuntimeException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        log.error(ex.getMessage());
    }
}
