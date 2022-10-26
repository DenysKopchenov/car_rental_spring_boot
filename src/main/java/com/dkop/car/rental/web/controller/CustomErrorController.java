package com.dkop.car.rental.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@Slf4j
public class CustomErrorController implements ErrorController {

    private static final String TITLE_ATTRIBUTE = "title";
    private static final List<Integer> availableErrorPages = List.of(400, 403, 404, 500);

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer status = availableErrorPages.stream()
                .filter(integer -> integer.equals(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)))
                .findFirst()
                .orElse(404);
        model.addAttribute(TITLE_ATTRIBUTE, status);
        return "error/" + status;
    }
}
