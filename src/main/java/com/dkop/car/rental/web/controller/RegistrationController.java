package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.dto.RegFormDto;
import com.dkop.car.rental.exception.UserAlreadyExists;
import com.dkop.car.rental.model.user.AppUser;
import com.dkop.car.rental.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/registration")
@Slf4j
public class RegistrationController {

    private static final String REGISTRATION_PAGE = "registration";
    private static final String USER = "appUser";
    private static final String TITLE_ATTRIBUTE = "title";
    private static final String TITLE_VALUE = "Registration";
    private static final String SUCCESS_REGISTRATION = "redirect:/registration?success";
    private static final String FAILED_REGISTRATION = "redirect:/registration?failed";
    private final UserService userService;


    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showRegistrationForm(@ModelAttribute(USER) RegFormDto regFormDto, Model model) {
        model.addAttribute(TITLE_ATTRIBUTE, TITLE_VALUE);
        return REGISTRATION_PAGE;
    }

    @PostMapping
    public String register(@ModelAttribute(USER) @Valid RegFormDto regFormDto,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return REGISTRATION_PAGE;
        }
        try {
            AppUser appUser = userService.saveAppUser(regFormDto);
            log.info("{} successfully registered", appUser.getEmail());
            return SUCCESS_REGISTRATION;
        } catch (UserAlreadyExists ex) {
            redirectAttributes.addFlashAttribute(USER, regFormDto);
            return FAILED_REGISTRATION;
        }
    }
}
