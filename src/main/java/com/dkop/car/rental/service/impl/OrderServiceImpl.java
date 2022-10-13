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
//        long pricePerDay = orderDto.getCarDto().getPricePerDay();
        long pricePerDay = orderDto.getCar().getPricePerDay();
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
//        rentOrder.setCar(mapper.mapCarDtoToCar(orderDto.getCarDto()));
        rentOrder.setCar(orderDto.getCar());
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
    public Page<RentOrder> findPagedOrdersByAppUserId(UUID appUserId, PaginationAndSortingBean paginationAndSortingBean, OrderFilterBean orderFilterBean) {
        PageRequest of = PageRequest.of(paginationAndSortingBean.getPage() - 1, paginationAndSortingBean.getSize(), Sort.by(Sort.Direction.valueOf(paginationAndSortingBean.getDirection()), paginationAndSortingBean.getSort()));
        return orderRepository.findOrdersByAppUserId(appUserId, of);
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
