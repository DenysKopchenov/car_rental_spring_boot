package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.dto.AppUserDto;
import com.dkop.car.rental.exception.UserAlreadyExistsException;
import com.dkop.car.rental.model.user.AppUser;
import com.dkop.car.rental.service.UserService;
import com.dkop.car.rental.util.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/profile")
@PreAuthorize("hasAuthority('USER')")
@Slf4j
public class ProfileController {

    private static final String USER_ATTRIBUTE = "appUser";
    private static final String EDIT_PROFILE = "profile/editProfile";
    private final UserService userService;
    private final Mapper mapper;

    public ProfileController(UserService userService, Mapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping
    public String showProfile(Model model, @AuthenticationPrincipal AppUser principal) {
        AppUser appUser = userService.findById(principal.getId());
        model.addAttribute(USER_ATTRIBUTE, mapper.mapAppUserToAppUserDto(appUser));
        return "profile/profile";
    }

    @GetMapping("/edit")
    public String showEditProfileForm(Model model, @AuthenticationPrincipal AppUser principal) {
        AppUser appUser = userService.findById(principal.getId());
        model.addAttribute(USER_ATTRIBUTE, mapper.mapAppUserToAppUserDto(appUser));
        return EDIT_PROFILE;
    }

    @PutMapping("/edit")
    public String editProfile(@ModelAttribute("appUser")
                              @Valid
                              AppUserDto appUserDto,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            return EDIT_PROFILE;
        }
        try {
            userService.updateUserProfile(appUserDto);
        } catch (UserAlreadyExistsException e) {
            model.addAttribute(USER_ATTRIBUTE, appUserDto);
            model.addAttribute("emailExist", appUserDto.getEmail());
            return EDIT_PROFILE;
        }
        return "redirect:/profile?success";
    }
}
