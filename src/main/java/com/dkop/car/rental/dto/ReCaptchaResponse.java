package com.dkop.car.rental.dto;

import lombok.Data;

@Data
public class ReCaptchaResponse {

    private boolean success;
    private String hostname;
    private String challengeTs;
}
