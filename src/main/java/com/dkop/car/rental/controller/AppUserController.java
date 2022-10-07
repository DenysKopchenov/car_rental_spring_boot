package com.dkop.car.rental.controller;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.model.client.AppUser;
import com.dkop.car.rental.model.order.RentOrder;
import com.dkop.car.rental.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class AppUserController {

    private final OrderService orderService;

    public AppUserController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/myOrders")
    public String showClientOrders(@ModelAttribute("order") OrderDto orderDto, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUser appUser = (AppUser) auth.getPrincipal();
        List<RentOrder> ordersByAppUserId = orderService.findOrdersByAppUserId(appUser.getId());
        model.addAttribute("userOrders", ordersByAppUserId);
        return "orders/orders";
    }
}
