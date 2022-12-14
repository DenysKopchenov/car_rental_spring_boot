package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.model.order.RentOrder;
import com.dkop.car.rental.model.user.AppUser;
import com.dkop.car.rental.service.OrderService;
import com.dkop.car.rental.util.Mapper;
import lombok.extern.slf4j.Slf4j;
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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('USER')")
@Slf4j
public class AppUserController {

    private static final String REDIRECT_ORDER_INFO_PAGE = "redirect:/order/{id}";
    private static final String TITLE = "title";
    private final OrderService orderService;
    private final Mapper mapper;

    public AppUserController(OrderService orderService, Mapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @GetMapping("/orders")
    public String showClientOrders(@ModelAttribute("pagination") PaginationAndSortingBean paginationAndSortingBean,
                                   @AuthenticationPrincipal AppUser appUser,
                                   Model model) {
        Page<RentOrder> userOrdersPaged = orderService.findOrdersByAppUserId(appUser.getId(), paginationAndSortingBean);
        List<OrderDto> userOrders = userOrdersPaged.stream()
                .map(mapper::mapRentOrderToOrderDto)
                .collect(Collectors.toList());
        model.addAttribute("userOrders", userOrders);
        model.addAttribute("numberOfPages", userOrdersPaged.getTotalPages());
        model.addAttribute(TITLE, "My orders");
        return "user/myOrders";
    }

    @PostMapping("/booking")
    public String bookCar(@ModelAttribute("order") @Valid OrderDto orderDto, BindingResult bindingResult) {
        LocalDate startDate = orderDto.getStartDate();
        LocalDate endDate = orderDto.getEndDate();
        if (startDate.isAfter(endDate) || startDate.isEqual(endDate)) {
            bindingResult.rejectValue("startDate", "start.date.error");
        }
        if (bindingResult.hasErrors()) {
            return "user/bookCarForm";
        }

        RentOrder order = orderService.saveOrder(orderDto);
        log.info("User {} created order {}", order.getAppUser().getId(), order.getId());
        return "redirect:/order/" + order.getId();
    }

    @PutMapping("/return/{id}")
    public String returnCar(@PathVariable("id") UUID orderId) {
        RentOrder order = orderService.askForReturn(orderId);
        log.info("User {} asked for return order {}", order.getAppUser().getId(), order.getId());
        return REDIRECT_ORDER_INFO_PAGE;
    }

    @PutMapping("/pay/order/{id}")
    public String payOrder(@PathVariable("id") UUID orderId) {
        RentOrder order = orderService.payOrder(orderId);
        log.info("User {} paid for order {}", order.getAppUser().getId(), order.getId());
        return REDIRECT_ORDER_INFO_PAGE;
    }

    @PutMapping("/pay/repair/{id}")
    public String payRepair(@PathVariable("id") UUID orderId) {
        RentOrder order = orderService.payRepair(orderId);
        log.info("User {} paid repair for order {}", order.getAppUser().getId(), order.getId());
        return REDIRECT_ORDER_INFO_PAGE;
    }
}
