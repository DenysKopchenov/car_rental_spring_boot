package com.dkop.car.rental.controller;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.model.order.OrderStatus;
import com.dkop.car.rental.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private static final String MANAGER_AUTHORITY = "hasAuthority('MANAGER')";
    private final OrderService orderService;

    public ManagerController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/pendingOrders")
    @PreAuthorize(MANAGER_AUTHORITY)
    public String showPendingOrders(@ModelAttribute("order") OrderDto orderDto) {
        orderService.findOrdersOrderStatus(OrderStatus.PENDING);
        return "orders/orders";
    }

    @GetMapping("/expiresToday")
    @PreAuthorize(MANAGER_AUTHORITY)
    public String showOrdersThatExpiresToday(@ModelAttribute("order") OrderDto orderDto) {
        orderService.showOrdersThatExpiresToday(LocalDate.now());
        return "orders/orders";
    }
}
