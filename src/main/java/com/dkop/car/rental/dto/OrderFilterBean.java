package com.dkop.car.rental.dto;

import com.dkop.car.rental.model.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderFilterBean {

    private List<OrderStatus> orderStatuses = Collections.emptyList();
}
