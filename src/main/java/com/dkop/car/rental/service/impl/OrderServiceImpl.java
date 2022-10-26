package com.dkop.car.rental.service.impl;

import com.dkop.car.rental.dto.OrderDto;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.Period;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private static final String DEFAULT_ORDER_SORT = "orderDetails.orderStatus";
    private static final String ORDER_NOT_FOUND = "Order not found";
    private static final String ORDER_ALREADY_EXISTS = "Order already exist";
    private static final String INVALID_ORDER_STATUS_PATTERN = "Invalid order status, must be %s";
    @Value("${price.for.driver}")
    private int priceForDriver;
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
        int days = countRentDays(orderDto);
        orderDto.setRentalPrice(pricePerDay * days);
        orderDto.setNumberOfRentDays(days);

    }

    @Override
    public RentOrder saveOrder(OrderDto orderDto) {
        RentOrder rentOrder = new RentOrder();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        rentOrder.setAppUser((AppUser) authentication.getPrincipal());
        rentOrder.setCar(orderDto.getCar());

        long pricePerDay = calculatePricePerDay(orderDto);
        if (orderDto.getRentalPrice() != pricePerDay * countRentDays(orderDto)) {
            throw new IllegalArgumentException("saveOrder() wrong price");
        }

        OrderDetails orderDetails = mapper.mapOrderDtoToOrderDetails(orderDto);
        orderDetails.setOrderStatus(OrderStatus.PENDING);
        rentOrder.setOrderDetails(orderDetails);

        if (isOrderAlreadyExist(rentOrder)) {
            throw new IllegalArgumentException("saveOrder() " + ORDER_ALREADY_EXISTS);
        }

        return orderRepository.save(rentOrder);
    }

    @Override
    public Page<RentOrder> findOrdersByAppUserId(UUID appUserId, PaginationAndSortingBean paginationAndSortingBean) {
        if (Objects.isNull(paginationAndSortingBean.getSort())) {
            paginationAndSortingBean.setSort(DEFAULT_ORDER_SORT);
        }
        PageRequest of = PageRequest.of(paginationAndSortingBean.getPage() - 1, paginationAndSortingBean.getSize(),
                Sort.by(Sort.Direction.valueOf(paginationAndSortingBean.getDirection()), paginationAndSortingBean.getSort()));
        return orderRepository.findOrdersByAppUserId(appUserId, of);
    }

    @Override
    public Page<RentOrder> findAllOrders(PaginationAndSortingBean paginationAndSortingBean) {
        if (Objects.isNull(paginationAndSortingBean.getSort())) {
            paginationAndSortingBean.setSort(DEFAULT_ORDER_SORT);
        }
        PageRequest of = PageRequest.of(paginationAndSortingBean.getPage() - 1,
                paginationAndSortingBean.getSize(), Sort.by(Sort.Direction.valueOf(paginationAndSortingBean.getDirection()), paginationAndSortingBean.getSort()));
        return orderRepository.findAll(of);
    }

    @Override
    @Transactional
    public RentOrder payOrder(UUID orderId) {
        RentOrder rentOrder = findById(orderId);
        OrderDetails orderDetails = rentOrder.getOrderDetails();
        if (orderDetails.getOrderStatus().equals(OrderStatus.AWAIT_PAYMENT)) {
            orderDetails.setOrderStatus(OrderStatus.PAID);
            return orderRepository.save(rentOrder);
        }
        throw new IllegalArgumentException("payOrder() " + String.format(INVALID_ORDER_STATUS_PATTERN, OrderStatus.AWAIT_PAYMENT));
    }

    @Override
    public RentOrder findById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("findById() " + ORDER_NOT_FOUND));
    }

    @Override
    @Transactional
    public RentOrder acceptOrder(UUID orderId) {
        RentOrder rentOrder = findById(orderId);
        OrderDetails orderDetails = rentOrder.getOrderDetails();
        if (orderDetails.getOrderStatus().equals(OrderStatus.PENDING)) {
            orderDetails.setOrderStatus(OrderStatus.AWAIT_PAYMENT);
            return orderRepository.save(rentOrder);
        }
        throw new IllegalArgumentException("acceptOrder() " + String.format(INVALID_ORDER_STATUS_PATTERN, OrderStatus.PAID));
    }

    @Override
    @Transactional
    public RentOrder rejectOrder(UUID orderId, String rejectDetails) {
        RentOrder rentOrder = findById(orderId);
        OrderDetails orderDetails = rentOrder.getOrderDetails();
        if (orderDetails.getOrderStatus().equals(OrderStatus.PENDING)) {
            orderDetails.setOrderStatus(OrderStatus.REJECTED);
            orderDetails.setRejectOrderDetails(rejectDetails);
            return orderRepository.save(rentOrder);
        }
        throw new IllegalArgumentException("rejectOrder() " + String.format(INVALID_ORDER_STATUS_PATTERN, OrderStatus.PENDING));
    }

    @Override
    @Transactional
    public RentOrder payRepair(UUID orderId) {
        RentOrder rentOrder = findById(orderId);
        OrderDetails orderDetails = rentOrder.getOrderDetails();
        if (orderDetails.getOrderStatus().equals(OrderStatus.AWAIT_REPAIR_PAYMENT)) {
            orderDetails.setOrderStatus(OrderStatus.REPAIR_PAID);
            RepairPayment repairPayment = orderDetails.getRepairPayment();
            String damageDescription = repairPayment.getDamageDescription();
            repairPayment.setDamageDescription(damageDescription + " was PAID");
            return orderRepository.save(rentOrder);
        }
        throw new IllegalArgumentException("payRepair() " + String.format(INVALID_ORDER_STATUS_PATTERN, OrderStatus.AWAIT_REPAIR_PAYMENT));
    }

    @Override
    @Transactional
    public RentOrder returnOrderWithoutDamage(UUID orderId) {
        RentOrder rentOrder = findById(orderId);
        OrderDetails orderDetails = rentOrder.getOrderDetails();
        if (orderDetails.getOrderStatus().equals(OrderStatus.CLIENT_WANT_RETURN)) {
            orderDetails.setOrderStatus(OrderStatus.COMPLETED);
            return orderRepository.save(rentOrder);
        }
        throw new IllegalArgumentException("returnOrderWithoutDamage() " + String.format(INVALID_ORDER_STATUS_PATTERN, OrderStatus.CLIENT_WANT_RETURN));
    }

    @Override
    @Transactional
    public RentOrder returnOrderWithDamage(UUID orderId, RepairPayment repairPayment) {
        RentOrder rentOrder = findById(orderId);
        OrderDetails orderDetails = rentOrder.getOrderDetails();
        if (orderDetails.getOrderStatus().equals(OrderStatus.CLIENT_WANT_RETURN)) {
            orderDetails.setRepairPayment(repairPayment);
            orderDetails.setOrderStatus(OrderStatus.AWAIT_REPAIR_PAYMENT);
            return orderRepository.save(rentOrder);
        }
        throw new IllegalArgumentException("returnOrderWithDamage() " + String.format(INVALID_ORDER_STATUS_PATTERN, OrderStatus.CLIENT_WANT_RETURN));
    }

    @Override
    @Transactional
    public RentOrder completeOrderWithPaidRepair(UUID orderId) {
        RentOrder rentOrder = findById(orderId);
        OrderDetails orderDetails = rentOrder.getOrderDetails();
        if (orderDetails.getOrderStatus().equals(OrderStatus.REPAIR_PAID)) {
            orderDetails.setOrderStatus(OrderStatus.COMPLETED);
            return orderRepository.save(rentOrder);
        }
        throw new IllegalArgumentException("completeOrderWithPaidRepair() " + String.format(INVALID_ORDER_STATUS_PATTERN, OrderStatus.REPAIR_PAID));
    }

    @Override
    public RentOrder askForReturn(UUID orderId) {
        RentOrder rentOrder = findById(orderId);
        OrderDetails orderDetails = rentOrder.getOrderDetails();
        if (orderDetails.getOrderStatus().equals(OrderStatus.PAID)) {
            orderDetails.setOrderStatus(OrderStatus.CLIENT_WANT_RETURN);
            return orderRepository.save(rentOrder);
        }
        throw new IllegalArgumentException("askForReturn() " + String.format(INVALID_ORDER_STATUS_PATTERN, OrderStatus.PAID));
    }

    private long calculatePricePerDay(OrderDto orderDto) {
        long pricePerDay = orderDto.getCar().getPricePerDay();
        if (orderDto.isWithDriver()) {
            return pricePerDay + priceForDriver;
        }
        return pricePerDay;
    }

    private int countRentDays(OrderDto orderDto) {
        return (int) ChronoUnit.DAYS.between(orderDto.getStartDate(), orderDto.getEndDate());
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
