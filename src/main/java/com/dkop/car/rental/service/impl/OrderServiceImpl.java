package com.dkop.car.rental.service.impl;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.model.client.AppUser;
import com.dkop.car.rental.model.order.OrderDetails;
import com.dkop.car.rental.model.order.OrderStatus;
import com.dkop.car.rental.model.order.RentOrder;
import com.dkop.car.rental.repository.OrderRepository;
import com.dkop.car.rental.service.OrderService;
import com.dkop.car.rental.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
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
        long pricePerDay = orderDto.getCarDto().getPricePerDay();
        if (orderDto.isWithDriver()) {
            pricePerDay += 30;
        }
        return pricePerDay;
    }

    @Override
    public void createOrder(OrderDto orderDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //validate all data
        RentOrder rentOrder = new RentOrder();
        rentOrder.setAppUser((AppUser) authentication.getPrincipal());
        rentOrder.setCar(mapper.mapCarDtoToCar(orderDto.getCarDto()));
        long pricePerDay = calculatePricePerDay(orderDto);
        if (orderDto.getRentalPrice() != pricePerDay * Period.between(orderDto.getStartDate(), orderDto.getEndDate()).getDays()) {
            throw new IllegalArgumentException();
        }
        OrderDetails orderDetails = mapper.mapOrderDtoToOrderDetails(orderDto);
        orderDetails.setOrderStatus(OrderStatus.PENDING);
        rentOrder.setOrderDetails(orderDetails);

        orderRepository.save(rentOrder);
    }

    @Override
    public List<RentOrder> findOrdersByAppUserId(UUID appUserId) {
        return orderRepository.findOrdersByAppUserId(appUserId);
    }

    @Override
    public List<RentOrder> findOrdersOrderStatus(OrderStatus orderStatus) {
        return orderRepository.findByOrderDetailsOrderStatus(orderStatus);
    }

    @Override
    public List<RentOrder> showOrdersThatExpiresToday(LocalDate now) {
        return orderRepository.findByOrderDetails_EndDateLessThanEqual(now);
    }
}
