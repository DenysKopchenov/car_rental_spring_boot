package com.dkop.car.rental.dto;

import com.dkop.car.rental.model.order.OrderStatus;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class OrderFilterBean {

    private List<OrderStatus> orderStatuses = Collections.emptyList();
}
