package com.dkop.car.rental.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationAndSortingBean {

    private Integer page = 1;
    private Integer size = 5;
    private String sort;
    private String direction = "ASC";
}
