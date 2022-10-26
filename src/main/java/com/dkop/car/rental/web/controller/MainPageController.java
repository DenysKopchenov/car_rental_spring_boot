package com.dkop.car.rental.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

    private final MessageSource messageSource;

    @Autowired
    public MainPageController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping
    public String homePage(Model model) {
        model.addAttribute("title", messageSource.getMessage("home", null, LocaleContextHolder.getLocale()));
        return "home";
    }
}
