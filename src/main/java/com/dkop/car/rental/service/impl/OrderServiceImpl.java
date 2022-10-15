package com.dkop.car.rental.service.impl;

import com.dkop.car.rental.dto.OrderDto;
import com.dkop.car.rental.dto.OrderFilterBean;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.model.order.OrderDetails;
import com.dkop.car.rental.model.order.OrderStatus;
import com.dkop.car.rental.model.order.RentOrder;
import com.dkop.car.rental.model.order.RepairPayment;
import com.dkop.car.rental.model.user.AppUser;
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

    @Override
    public RentOrder saveOrder(OrderDto orderDto) {
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

    @Override
    public Page<RentOrder> findOrdersByAppUserId(UUID appUserId, PaginationAndSortingBean paginationAndSortingBean, OrderFilterBean orderFilterBean) {
        if (Objects.isNull(paginationAndSortingBean.getSort())) {
            paginationAndSortingBean.setSort("orderDetails.orderStatus");
        }
        PageRequest of = PageRequest.of(paginationAndSortingBean.getPage() - 1, paginationAndSortingBean.getSize(),
                Sort.by(Sort.Direction.valueOf(paginationAndSortingBean.getDirection()), paginationAndSortingBean.getSort()));
        return orderRepository.findOrdersByAppUserId(appUserId, of);
    }

    @Override
    public Page<RentOrder> findAllOrders(PaginationAndSortingBean paginationAndSortingBean, OrderFilterBean orderFilterBean) {
        if (Objects.isNull(paginationAndSortingBean.getSort())) {
            paginationAndSortingBean.setSort("orderDetails.orderStatus");
        }
        PageRequest of = PageRequest.of(paginationAndSortingBean.getPage() - 1,
                paginationAndSortingBean.getSize(), Sort.by(Sort.Direction.valueOf(paginationAndSortingBean.getDirection()), paginationAndSortingBean.getSort()));
        return orderRepository.findAll(of);
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
            throw new IllegalArgumentException("Invalid order status, must be AWAIT_PAYMENT");
        }
        throw new EntityNotFoundException("Order not found");
    }

    @Override
    public RentOrder findById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
    }

//    @Override
//    public RentOrder changeOrderStatus(UUID orderId, OrderStatus oldStatus, OrderStatus newStatus) {
//        Optional<RentOrder> orderOptional = orderRepository.findById(orderId);
//        if (orderOptional.isPresent()) {
//            RentOrder rentOrder = orderOptional.get();
//            OrderDetails orderDetails = rentOrder.getOrderDetails();
//            if (orderDetails.getOrderStatus().equals(oldStatus)) {
//                orderDetails.setOrderStatus(newStatus);
//                return orderRepository.save(rentOrder);
//            }
//            throw new IllegalArgumentException("Invalid order status, must be " + oldStatus.name());
//        }
//        throw new EntityNotFoundException("Order not found");
//    }

    @Override
    public RentOrder acceptOrder(UUID orderId) {
        Optional<RentOrder> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            RentOrder rentOrder = orderOptional.get();
            OrderDetails orderDetails = rentOrder.getOrderDetails();
            if (orderDetails.getOrderStatus().equals(OrderStatus.PENDING)) {
                orderDetails.setOrderStatus(OrderStatus.AWAIT_PAYMENT);
                return orderRepository.save(rentOrder);
            }
            throw new IllegalArgumentException("Invalid order status, must be PAID");
        }
        throw new EntityNotFoundException("Order not found");
    }

    @Override
    public RentOrder rejectOrder(UUID orderId, String rejectDetails) {
        Optional<RentOrder> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            RentOrder rentOrder = orderOptional.get();
            OrderDetails orderDetails = rentOrder.getOrderDetails();
            if (orderDetails.getOrderStatus().equals(OrderStatus.PENDING)) {
                orderDetails.setOrderStatus(OrderStatus.REJECTED);
                orderDetails.setRejectOrderDetails(rejectDetails);
                return orderRepository.save(rentOrder);
            }
            throw new IllegalArgumentException("Invalid order status, must be PENDING");
        }
        throw new EntityNotFoundException("Order not found");
    }

    @Override
    public RentOrder payRepair(UUID orderId) {
        Optional<RentOrder> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            RentOrder rentOrder = orderOptional.get();
            OrderDetails orderDetails = rentOrder.getOrderDetails();
            if (orderDetails.getOrderStatus().equals(OrderStatus.AWAIT_REPAIR_PAYMENT)) {
                orderDetails.setOrderStatus(OrderStatus.REPAIR_PAID);
                return orderRepository.save(rentOrder);
            }
            throw new IllegalArgumentException("Invalid order status, must be AWAIT_REPAIR_PAYMENT");
        }
        throw new EntityNotFoundException("Order not found");
    }

    @Override
    public RentOrder returnOrder(UUID orderId, RepairPayment repairPayment) {
        return null;
    }

    private static long calculatePricePerDay(OrderDto orderDto) {
        long pricePerDay = orderDto.getCar().getPricePerDay();
        if (orderDto.isWithDriver()) {
            pricePerDay += 30;
        }
        return pricePerDay;
    }

    private boolean isOrderAlreadyExist(RentOrder rentOrder) {
        return orderRepository.isOrderExist(rentOrder.getCar().getId(),
                rentOrder.getAppUser().getId(),
                rentOrder.getOrderDetails().getStartDate(),
                rentOrder.getOrderDetails().getEndDate(),
                rentOrder.getOrderDetails().isWithDriver(),
                rentOrder.getOrderDetails().getOrderStatus());
    }
}
