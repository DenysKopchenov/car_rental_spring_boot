package com.dkop.car.rental.service.impl;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.dto.OrderFilterBean;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.model.client.AppUser;
import com.dkop.car.rental.model.order.OrderDetails;
import com.dkop.car.rental.model.order.OrderStatus;
import com.dkop.car.rental.model.order.RentOrder;
import com.dkop.car.rental.repository.OrderRepository;
import com.dkop.car.rental.service.OrderService;
import com.dkop.car.rental.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.Period;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final Mapper mapper;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, Mapper mapper) {
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }

    @Override
    public void calculateOrder(OrderDto orderDto) {
        long pricePerDay = calculatePricePerDay(orderDto);
        long totalRentalPrice = pricePerDay * Period.between(orderDto.getStartDate(), orderDto.getEndDate()).getDays();
        orderDto.setRentalPrice(totalRentalPrice);
    }

    private static long calculatePricePerDay(OrderDto orderDto) {
        long pricePerDay = orderDto.getCar().getPricePerDay();
        if (orderDto.isWithDriver()) {
            pricePerDay += 30;
        }
        return pricePerDay;
    }

    @Override
    public RentOrder createOrder(OrderDto orderDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RentOrder rentOrder = new RentOrder();
        rentOrder.setAppUser((AppUser) authentication.getPrincipal());
        rentOrder.setCar(orderDto.getCar());

        long pricePerDay = calculatePricePerDay(orderDto);
        if (orderDto.getRentalPrice() != pricePerDay * Period.between(orderDto.getStartDate(), orderDto.getEndDate()).getDays()) {
            throw new IllegalArgumentException("Wrong total price!?!?!?!?");
        }

        OrderDetails orderDetails = mapper.mapOrderDtoToOrderDetails(orderDto);
        orderDetails.setOrderStatus(OrderStatus.PENDING);
        rentOrder.setOrderDetails(orderDetails);

        if (isOrderAlreadyExist(rentOrder)) {
            throw new IllegalArgumentException("Order already exist");
        }

        return orderRepository.save(rentOrder);
    }

    private boolean isOrderAlreadyExist(RentOrder rentOrder) {
        return orderRepository.isOrderExist(rentOrder.getCar().getId(),
                rentOrder.getAppUser().getId(),
                rentOrder.getOrderDetails().getStartDate(),
                rentOrder.getOrderDetails().getEndDate(),
                rentOrder.getOrderDetails().isWithDriver(),
                rentOrder.getOrderDetails().getOrderStatus());
    }

    @Override
    public Page<RentOrder> findOrdersByAppUserId(UUID appUserId, PaginationAndSortingBean paginationAndSortingBean, OrderFilterBean orderFilterBean) {
        if (Objects.isNull(paginationAndSortingBean.getSort())) {
            paginationAndSortingBean.setSort("orderDetails.orderStatus");
        }
        PageRequest of = PageRequest.of(paginationAndSortingBean.getPage() - 1, paginationAndSortingBean.getSize(), Sort.by(Sort.Direction.valueOf(paginationAndSortingBean.getDirection()), paginationAndSortingBean.getSort()));
        return orderRepository.findOrdersByAppUserId(appUserId, of);
    }

    @Override
    public Page<RentOrder> findAllOrders(PaginationAndSortingBean paginationAndSortingBean, OrderFilterBean orderFilterBean) {
        return null;
    }

    @Override
    public RentOrder payOrder(UUID orderId) {
        Optional<RentOrder> byId = orderRepository.findById(orderId);
        if (byId.isPresent()) {
            RentOrder rentOrder = byId.get();
            OrderDetails orderDetails = rentOrder.getOrderDetails();
            if (orderDetails.getOrderStatus().equals(OrderStatus.AWAIT_PAYMENT)) {
                orderDetails.setOrderStatus(OrderStatus.PAID);
                return orderRepository.save(rentOrder);
            }
        }
        throw new EntityNotFoundException("Order not found");
    }

    @Override
    public RentOrder findById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
    }
}
