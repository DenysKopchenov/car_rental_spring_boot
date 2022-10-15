package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.model.car.Car;
import com.dkop.car.rental.model.order.RentOrder;
import com.dkop.car.rental.service.CarService;
import com.dkop.car.rental.service.OrderService;
import com.dkop.car.rental.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/order")
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

    @GetMapping("/bookCar")
    public String showPayment(@ModelAttribute("order") OrderDto orderDto) {
        orderService.calculateOrder(orderDto);
        return "user/bookCarForm";
    }

    @PostMapping("/bookCar")
    @PreAuthorize("hasAuthority('USER')")
    public String bookCar(@ModelAttribute("order") @Valid OrderDto orderDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/bookCarForm";
        }
        RentOrder order = orderService.saveOrder(orderDto);
        return "redirect:/order/" + order.getId();
    }

    @PutMapping("/payOrder/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public String payOrder(@PathVariable("id") UUID orderId, Model model) {
        RentOrder rentOrder = orderService.payOrder(orderId);
        model.addAttribute("order", mapper.mapRentOrderToOrderDto(rentOrder));
        return "redirect:/order/{id}";
    }

    @PutMapping("/payRepair/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public String payRepair(@PathVariable("id") UUID orderId, Model model) {
        RentOrder rentOrder = orderService.payRepair(orderId);
        model.addAttribute("order", mapper.mapRentOrderToOrderDto(rentOrder));
        return "redirect:/order/{id}";
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showOrder(@PathVariable("id") UUID orderId, Model model) {
        RentOrder order = orderService.findById(orderId);
        OrderDto orderDto = mapper.mapRentOrderToOrderDto(order);

        model.addAttribute("order", orderDto);
        return "orders/orderInfo";
    }

    @PutMapping("/accept/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public String acceptOrder(@PathVariable("id") UUID orderId) {
        orderService.acceptOrder(orderId);
        return "redirect:/order/{id}";
    }

    @GetMapping("/reject/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public String showRejectOrderForm(@PathVariable("id") UUID orderId, Model model) {
        RentOrder order = orderService.findById(orderId);
        OrderDto orderDto = mapper.mapRentOrderToOrderDto(order);

        model.addAttribute("order", orderDto);
        return "manager/rejectOrderForm";
    }

    @PutMapping("/reject/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public String submitReject(@PathVariable("id") UUID orderId,
                               @ModelAttribute("rejectDetails")
                               String rejectDetails, RedirectAttributes redirectAttributes) {
        if (rejectDetails.isBlank()) {
            redirectAttributes.addFlashAttribute("rejectDetailsError", "Must not be empty");
            return "redirect:/order/reject/{id}";
        }
        orderService.rejectOrder(orderId, rejectDetails);
        return "redirect:/order/{id}";
    }

    @GetMapping("/return/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public String showReturnOrderForm(@PathVariable("id") UUID orderId, Model model) {
        RentOrder order = orderService.findById(orderId);
        OrderDto orderDto = mapper.mapRentOrderToOrderDto(order);

        model.addAttribute("order", orderDto);
        return "manager/returnOrderForm";
    }

    @PutMapping("/return/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public String submitReturn(@PathVariable("id") UUID orderId, @ModelAttribute("order") OrderDto orderDto) {
        orderService.returnOrder(orderId, orderDto.getRepairPayment());
        return "redirect:/order/{id}";
    }
}
