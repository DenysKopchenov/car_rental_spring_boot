package com.dkop.car.rental.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReCaptchaResponse {

    private boolean success;
    private String hostname;
    private String challenge_ts;
}
