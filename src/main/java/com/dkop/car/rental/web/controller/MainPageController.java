package com.dkop.car.rental.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class MainPageController {

    private static final String TITLE_ATTRIBUTE = "title";
    private static final String TITLE_VALUE = "Car rental";
    private static final String HOME_PAGE = "home";

    @GetMapping
    public String homePage(Model model) {
        model.addAttribute(TITLE_ATTRIBUTE, TITLE_VALUE);
        return HOME_PAGE;
    }
}
