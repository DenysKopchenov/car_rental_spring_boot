package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.model.order.OrderDetails;
import com.dkop.car.rental.model.order.RentOrder;
import com.dkop.car.rental.model.order.RepairPayment;
import com.dkop.car.rental.service.OrderService;
import com.dkop.car.rental.util.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;
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

    @GetMapping("/orders")
    public String showClientOrders(@ModelAttribute("pagination") PaginationAndSortingBean paginationAndSortingBean,
                                   Model model) {
        Page<RentOrder> allOrdersPaged = orderService.findAllOrders(paginationAndSortingBean);
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

    @PutMapping("/accept/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public String acceptOrder(@PathVariable("id") UUID orderId, Model model) {
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
                               String rejectDetails,
                               Model model) {
        if (rejectDetails.isBlank()) {
            RentOrder rentOrder = orderService.findById(orderId);
            model.addAttribute("order", mapper.mapRentOrderToOrderDto(rentOrder));
            model.addAttribute("rejectDetailsError", "Must not be empty");
            return "manager/rejectOrderForm";
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
        RepairPayment repairPayment = orderDto.getRepairPayment();
        if (repairPayment.getRepairCost() == 0) {
            orderService.returnOrderWithoutDamage(orderId);
            return "redirect:/order/{id}";
        }
        orderService.returnOrderWithDamage(orderId, orderDto.getRepairPayment());
        return "redirect:/order/{id}";
    }

    @PutMapping("/complete/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public String completeRepairPaidOrder(@PathVariable("id") UUID orderId) {
        orderService.completeRepairPaidOrder(orderId);
        return "redirect:/order/{id}";
    }
}
