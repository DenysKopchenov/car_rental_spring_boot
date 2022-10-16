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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.Period;
import java.util.Objects;
import java.util.Optional;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RentOrder rentOrder = new RentOrder();
        rentOrder.setAppUser((AppUser) authentication.getPrincipal());
        rentOrder.setCar(orderDto.getCar());

        long pricePerDay = calculatePricePerDay(orderDto);
        if (orderDto.getRentalPrice() != pricePerDay * countRentDays(orderDto)) {
            throw new IllegalArgumentException();
        }

        OrderDetails orderDetails = mapper.mapOrderDtoToOrderDetails(orderDto);
        orderDetails.setOrderStatus(OrderStatus.PENDING);
        rentOrder.setOrderDetails(orderDetails);

        if (isOrderAlreadyExist(rentOrder)) {
            throw new IllegalArgumentException(ORDER_ALREADY_EXISTS);
        }

        return orderRepository.save(rentOrder);
    }

    @Override
    public Page<RentOrder> findOrdersByAppUserId(UUID appUserId, PaginationAndSortingBean paginationAndSortingBean, OrderFilterBean orderFilterBean) {
        if (Objects.isNull(paginationAndSortingBean.getSort())) {
            paginationAndSortingBean.setSort(DEFAULT_ORDER_SORT);
        }
        PageRequest of = PageRequest.of(paginationAndSortingBean.getPage() - 1, paginationAndSortingBean.getSize(),
                Sort.by(Sort.Direction.valueOf(paginationAndSortingBean.getDirection()), paginationAndSortingBean.getSort()));
        return orderRepository.findOrdersByAppUserId(appUserId, of);
    }

    @Override
    public Page<RentOrder> findAllOrders(PaginationAndSortingBean paginationAndSortingBean, OrderFilterBean orderFilterBean) {
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
        Optional<RentOrder> byId = orderRepository.findById(orderId);
        if (byId.isPresent()) {
            RentOrder rentOrder = byId.get();
            OrderDetails orderDetails = rentOrder.getOrderDetails();
            if (orderDetails.getOrderStatus().equals(OrderStatus.AWAIT_PAYMENT)) {
                orderDetails.setOrderStatus(OrderStatus.PAID);
                return orderRepository.save(rentOrder);
            }
            throw new IllegalArgumentException(String.format(INVALID_ORDER_STATUS_PATTERN, OrderStatus.AWAIT_PAYMENT));
        }
        throw new EntityNotFoundException(ORDER_NOT_FOUND);
    }

    @Override
    public RentOrder findById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    @Transactional
    public RentOrder acceptOrder(UUID orderId) {
        Optional<RentOrder> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            RentOrder rentOrder = orderOptional.get();
            OrderDetails orderDetails = rentOrder.getOrderDetails();
            if (orderDetails.getOrderStatus().equals(OrderStatus.PENDING)) {
                orderDetails.setOrderStatus(OrderStatus.AWAIT_PAYMENT);
                return orderRepository.save(rentOrder);
            }
            throw new IllegalArgumentException(String.format(INVALID_ORDER_STATUS_PATTERN, OrderStatus.PAID));
        }
        throw new EntityNotFoundException(ORDER_NOT_FOUND);
    }

    @Override
    @Transactional
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
            throw new IllegalArgumentException(String.format(INVALID_ORDER_STATUS_PATTERN, OrderStatus.PENDING));
        }
        throw new EntityNotFoundException(ORDER_NOT_FOUND);
    }

    @Override
    @Transactional
    public RentOrder payRepair(UUID orderId) {
        Optional<RentOrder> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            RentOrder rentOrder = orderOptional.get();
            OrderDetails orderDetails = rentOrder.getOrderDetails();
            if (orderDetails.getOrderStatus().equals(OrderStatus.AWAIT_REPAIR_PAYMENT)) {
                orderDetails.setOrderStatus(OrderStatus.REPAIR_PAID);
                RepairPayment repairPayment = orderDetails.getRepairPayment();
                String damageDescription = repairPayment.getDamageDescription();
                repairPayment.setDamageDescription("PAID " + damageDescription);
                return orderRepository.save(rentOrder);
            }
            throw new IllegalArgumentException(String.format(INVALID_ORDER_STATUS_PATTERN, OrderStatus.AWAIT_REPAIR_PAYMENT));
        }
        throw new EntityNotFoundException(ORDER_NOT_FOUND);
    }

    @Override
    @Transactional
    public RentOrder returnOrderWithoutDamage(UUID orderId) {
        Optional<RentOrder> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            RentOrder rentOrder = orderOptional.get();
            OrderDetails orderDetails = rentOrder.getOrderDetails();
            if (orderDetails.getOrderStatus().equals(OrderStatus.PAID)) {
                orderDetails.setOrderStatus(OrderStatus.COMPLETED);
                return orderRepository.save(rentOrder);
            }
            throw new IllegalArgumentException(String.format(INVALID_ORDER_STATUS_PATTERN, OrderStatus.PAID));
        }
        throw new EntityNotFoundException(ORDER_NOT_FOUND);
    }

    @Override
    @Transactional
    public RentOrder returnOrderWithDamage(UUID orderId, RepairPayment repairPayment) {
        Optional<RentOrder> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            RentOrder rentOrder = orderOptional.get();
            OrderDetails orderDetails = rentOrder.getOrderDetails();
            if (orderDetails.getOrderStatus().equals(OrderStatus.PAID)) {
                orderDetails.setRepairPayment(repairPayment);
                orderDetails.setOrderStatus(OrderStatus.AWAIT_REPAIR_PAYMENT);
                return orderRepository.save(rentOrder);
            }

            throw new IllegalArgumentException(String.format(INVALID_ORDER_STATUS_PATTERN, OrderStatus.PAID));
        }
        throw new EntityNotFoundException(ORDER_NOT_FOUND);
    }

    @Override
    @Transactional
    public RentOrder completeRepairPaidOrder(UUID orderId) {
        Optional<RentOrder> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            RentOrder rentOrder = orderOptional.get();
            OrderDetails orderDetails = rentOrder.getOrderDetails();
            if (orderDetails.getOrderStatus().equals(OrderStatus.REPAIR_PAID)) {
                orderDetails.setOrderStatus(OrderStatus.COMPLETED);
                return orderRepository.save(rentOrder);
            }

            throw new IllegalArgumentException(String.format(INVALID_ORDER_STATUS_PATTERN, OrderStatus.PAID));
        }
        throw new EntityNotFoundException(ORDER_NOT_FOUND);
    }

    private long calculatePricePerDay(OrderDto orderDto) {
        long pricePerDay = orderDto.getCar().getPricePerDay();
        if (orderDto.isWithDriver()) {
            pricePerDay += priceForDriver;
        }
        return pricePerDay;
    }

    private int countRentDays(OrderDto orderDto) {
        return Period.between(orderDto.getStartDate(), orderDto.getEndDate()).getDays();
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
