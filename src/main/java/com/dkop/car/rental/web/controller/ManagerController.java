package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.dto.OrderFilterBean;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.model.order.OrderDetails;
import com.dkop.car.rental.model.order.RentOrder;
import com.dkop.car.rental.service.OrderService;
import com.dkop.car.rental.util.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager")
@PreAuthorize("hasAuthority('MANAGER')")
public class ManagerController {
    private final OrderService orderService;
    private final Mapper mapper;

    public ManagerController(OrderService orderService, Mapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @GetMapping("/allOrders")
    public String showClientOrders(@ModelAttribute("pagination") PaginationAndSortingBean paginationAndSortingBean,
                                   @ModelAttribute("filter") OrderFilterBean orderFilterBean,
                                   Model model) {
        Page<RentOrder> allOrdersPaged = orderService.findAllOrders(paginationAndSortingBean, orderFilterBean);
        List<OrderDto> allOrders = allOrdersPaged.stream()
                .map(order -> {
                    OrderDto orderDto = new OrderDto();
                    orderDto.setId(order.getId());
                    orderDto.setCar(order.getCar());
                    orderDto.setAppUserDto(mapper.mapAppUserToAppUserDto(order.getAppUser()));
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
        model.addAttribute("orders", allOrders);
        model.addAttribute("numberOfPages", allOrdersPaged.getTotalPages());
        return "manager/allOrders";
    }
}
