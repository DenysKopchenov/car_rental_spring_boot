package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.dto.OrderFilterBean;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.model.order.RentOrder;
import com.dkop.car.rental.model.user.AppUser;
import com.dkop.car.rental.service.OrderService;
import com.dkop.car.rental.util.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('USER')")
public class AppUserController {

    private final OrderService orderService;
    private final Mapper mapper;

    public AppUserController(OrderService orderService, Mapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @GetMapping("/orders")
    public String showClientOrders(@ModelAttribute("pagination") PaginationAndSortingBean paginationAndSortingBean,
                                   @ModelAttribute("filter") OrderFilterBean orderFilterBean,
                                   @AuthenticationPrincipal AppUser appUser,
                                   Model model) {
        Page<RentOrder> userOrdersPaged = orderService.findOrdersByAppUserId(appUser.getId(), paginationAndSortingBean, orderFilterBean);
        List<OrderDto> userOrders = userOrdersPaged.stream()
                .map(mapper::mapRentOrderToOrderDto)
                .collect(Collectors.toList());
        model.addAttribute("userOrders", userOrders);
        model.addAttribute("numberOfPages", userOrdersPaged.getTotalPages());
        return "user/myOrders";
    }

    @PostMapping("/booking")
    @PreAuthorize("hasAuthority('USER')")
    public String bookCar(@ModelAttribute("order") @Valid OrderDto orderDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/bookCarForm";
        }
        RentOrder order = orderService.saveOrder(orderDto);
        return "redirect:/order/" + order.getId();
    }

    @PutMapping("/pay/order/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public String payOrder(@PathVariable("id") UUID orderId, Model model) {
        RentOrder rentOrder = orderService.payOrder(orderId);
        return "redirect:/order/{id}";
    }

    @PutMapping("/pay/repair/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public String payRepair(@PathVariable("id") UUID orderId, Model model) {
        RentOrder rentOrder = orderService.payRepair(orderId);
        return "redirect:/order/{id}";
    }
}
