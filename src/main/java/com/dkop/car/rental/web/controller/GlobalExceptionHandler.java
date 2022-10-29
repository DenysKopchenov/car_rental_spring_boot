package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.exception.OrderAlreadyExistsException;
import com.dkop.car.rental.exception.ReCaptchaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public void handleEntityNotFoundException(EntityNotFoundException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        log.error(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(IllegalArgumentException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        log.error(ex.getMessage());
    }

    @ExceptionHandler(OrderAlreadyExistsException.class)
    public void handleOrderAlreadyExistsException(OrderAlreadyExistsException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        log.error(ex.getMessage());
    }

    @ExceptionHandler(ReCaptchaException.class)
    public void handleReCaptchaException(ReCaptchaException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        log.error(ex.getMessage());
    }
}
