package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private static final String MANAGER_AUTHORITY = "hasAuthority('MANAGER')";
    private final OrderService orderService;

    public ManagerController(OrderService orderService) {
        this.orderService = orderService;
    }
}
