package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.model.order.RentOrder;
import com.dkop.car.rental.model.order.RepairPayment;
import com.dkop.car.rental.model.user.AppUser;
import com.dkop.car.rental.service.OrderService;
import com.dkop.car.rental.util.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager")
@PreAuthorize("hasAuthority('MANAGER')")
@Slf4j
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
    public String acceptOrder(@PathVariable("id") UUID orderId,
                              @AuthenticationPrincipal AppUser manager) {
        RentOrder rentOrder = orderService.acceptOrder(orderId);
        log.info("Order {} accepted by manager {}", rentOrder.getId(), manager.getId());
        return REDIRECT_ORDER_INFO_PAGE;
    }

    @GetMapping("/reject/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public String showRejectOrderForm(@PathVariable("id") UUID orderId, Model model) {
        RentOrder rentOrder = orderService.findById(orderId);
        OrderDto orderDto = mapper.mapRentOrderToOrderDto(rentOrder);
        model.addAttribute(ORDER_ATTRIBUTE, orderDto);
        model.addAttribute(TITLE_ATTRIBUTE, "Reject order");
        return "manager/rejectOrderForm";
    }

    @PutMapping("/reject/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public String submitReject(@PathVariable("id") UUID orderId,
                               @ModelAttribute("rejectDetails") String rejectDetails,
                               Model model,
                               @AuthenticationPrincipal AppUser manager) {
        if (rejectDetails.isBlank()) {
            model.addAttribute("rejectDetailsError", "Add reject details");
            return showRejectOrderForm(orderId, model);
        }
        RentOrder rentOrder = orderService.rejectOrder(orderId, rejectDetails);
        log.info("Order {} rejected by manager {}", rentOrder.getId(), manager.getId());
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
    public String submitReturn(@PathVariable("id") UUID orderId,
                               @ModelAttribute("order") OrderDto orderDto,
                               @AuthenticationPrincipal AppUser manager) {
        RepairPayment repairPayment = orderDto.getRepairPayment();
        if (repairPayment.getRepairCost() <= 0) {
            RentOrder rentOrder = orderService.returnOrderWithoutDamage(orderId);
            log.info("Order {} completed by manager {} without damage", rentOrder.getId(), manager.getId());
            return REDIRECT_ORDER_INFO_PAGE;
        }
        RentOrder rentOrder = orderService.returnOrderWithDamage(orderId, repairPayment);
        log.info("Order {} returned by manager {} with repair cost {}", rentOrder.getId(), manager.getId(), repairPayment.getRepairCost());
        return REDIRECT_ORDER_INFO_PAGE;
    }

    @PutMapping("/complete/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public String completeRepairPaidOrder(@PathVariable("id") UUID orderId,
                                          @AuthenticationPrincipal AppUser manager) {
        RentOrder rentOrder = orderService.completeOrderWithPaidRepair(orderId);
        log.info("Order {} completed by manager {} with repair cost {}", rentOrder.getId(), manager.getId(), rentOrder.getOrderDetails().getRepairPayment().getRepairCost());
        return REDIRECT_ORDER_INFO_PAGE;
    }

    @ExceptionHandler(RuntimeException.class)
    public void handleRuntimeException(RuntimeException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        log.error(ex.getMessage());
    }
}
