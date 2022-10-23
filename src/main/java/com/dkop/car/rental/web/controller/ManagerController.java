package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
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
    private static final String REDIRECT_ORDER_INFO_PAGE = "redirect:/order/{id}";
    private static final String TITLE_ATTRIBUTE = "title";
    private static final String ORDER_ATTRIBUTE = "order";
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
                .map(mapper::mapRentOrderToOrderDto
                )
                .collect(Collectors.toList());
        model.addAttribute("orders", allOrders);
        model.addAttribute("numberOfPages", allOrdersPaged.getTotalPages());
        model.addAttribute(TITLE_ATTRIBUTE, "Orders");
        return "manager/allOrders";
    }

    @PutMapping("/accept/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public String acceptOrder(@PathVariable("id") UUID orderId) {
        orderService.acceptOrder(orderId);
        return REDIRECT_ORDER_INFO_PAGE;
    }

    @GetMapping("/reject/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public String showRejectOrderForm(@PathVariable("id") UUID orderId, Model model) {
        RentOrder order = orderService.findById(orderId);
        OrderDto orderDto = mapper.mapRentOrderToOrderDto(order);

        model.addAttribute(ORDER_ATTRIBUTE, orderDto);
        model.addAttribute(TITLE_ATTRIBUTE, "Reject order");
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
            model.addAttribute(ORDER_ATTRIBUTE, mapper.mapRentOrderToOrderDto(rentOrder));
            model.addAttribute("rejectDetailsError", "Must not be empty");
            return "manager/rejectOrderForm";
        }
        orderService.rejectOrder(orderId, rejectDetails);
        return REDIRECT_ORDER_INFO_PAGE;
    }

    @GetMapping("/return/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public String showReturnOrderForm(@PathVariable("id") UUID orderId, Model model) {
        RentOrder order = orderService.findById(orderId);
        OrderDto orderDto = mapper.mapRentOrderToOrderDto(order);

        model.addAttribute(ORDER_ATTRIBUTE, orderDto);
        model.addAttribute(TITLE_ATTRIBUTE, "Return order");
        return "manager/returnOrderForm";
    }

    @PutMapping("/return/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public String submitReturn(@PathVariable("id") UUID orderId, @ModelAttribute("order") OrderDto orderDto) {
        RepairPayment repairPayment = orderDto.getRepairPayment();
        if (repairPayment.getRepairCost() <= 0) {
            orderService.returnOrderWithoutDamage(orderId);
            return REDIRECT_ORDER_INFO_PAGE;
        }
        orderService.returnOrderWithDamage(orderId, repairPayment);
        return REDIRECT_ORDER_INFO_PAGE;
    }

    @PutMapping("/complete/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public String completeRepairPaidOrder(@PathVariable("id") UUID orderId) {
        orderService.completeRepairPaidOrder(orderId);
        return REDIRECT_ORDER_INFO_PAGE;
    }
}
