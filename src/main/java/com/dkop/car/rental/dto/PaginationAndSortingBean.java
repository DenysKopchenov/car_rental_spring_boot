package com.dkop.car.rental.dto;

import lombok.Data;

@Data
public class PaginationAndSortingBean {

    private Integer page = 1;
    private Integer size = 4;
    private String sort;
    private String direction = "ASC";
}
