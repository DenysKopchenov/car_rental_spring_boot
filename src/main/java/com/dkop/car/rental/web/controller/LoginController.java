package com.dkop.car.rental.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {

    private final MessageSource messageSource;

    @Autowired
    public LoginController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping
    public String login(Model model) {
        model.addAttribute("title", messageSource.getMessage("login", null, LocaleContextHolder.getLocale()));
        return "login";
    }
}
