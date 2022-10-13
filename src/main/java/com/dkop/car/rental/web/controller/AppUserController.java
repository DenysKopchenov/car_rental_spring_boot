package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.dto.OrderFilterBean;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.model.client.AppUser;
import com.dkop.car.rental.model.order.OrderDetails;
import com.dkop.car.rental.model.order.RentOrder;
import com.dkop.car.rental.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('USER')")
public class AppUserController {

    private final OrderService orderService;

    public AppUserController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/myOrders")
    public String showClientOrders(@ModelAttribute("pagination") PaginationAndSortingBean paginationAndSortingBean,
                                   @ModelAttribute("filter") OrderFilterBean orderFilterBean,
                                   @AuthenticationPrincipal AppUser appUser,
                                   Model model) {
        if (Objects.isNull(paginationAndSortingBean.getSort())) {
            paginationAndSortingBean.setSort("orderDetails.orderStatus");
        }
        Page<RentOrder> userOrdersPaged = orderService.findPagedOrdersByAppUserId(appUser.getId(), paginationAndSortingBean, orderFilterBean);
        List<OrderDto> userOrders = userOrdersPaged.stream()
                .map(order -> {
                    OrderDto orderDto = new OrderDto();
                    orderDto.setCar(order.getCar());

                    OrderDetails orderDetails = order.getOrderDetails();
                    orderDto.setWithDriver(orderDetails.isWithDriver());
                    orderDto.setStartDate(orderDetails.getStartDate());
                    orderDto.setEndDate(orderDetails.getEndDate());
                    orderDto.setRentalPrice(orderDetails.getRentalPrice());
                    orderDto.setOrderStatus(orderDetails.getOrderStatus());
                    orderDto.setRepairPayment(orderDetails.getRepairPayment());
                    return orderDto;
                })
                .collect(Collectors.toList());
        model.addAttribute("userOrders", userOrders);
        model.addAttribute("numberOfPages", userOrdersPaged.getTotalPages());
        return "user/myOrders";
    }
}
