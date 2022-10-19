package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.dto.AppUserDto;
import com.dkop.car.rental.model.user.AppUser;
import com.dkop.car.rental.service.UserService;
import com.dkop.car.rental.util.Mapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/profile")
@PreAuthorize("hasAuthority('USER')")
public class ProfileController {

    private final UserService userService;
    private final Mapper mapper;

    public ProfileController(UserService userService, Mapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping
    public String showProfile(Model model, @AuthenticationPrincipal AppUser principal) {
        AppUser appUser = userService.findById(principal.getId());
        model.addAttribute("appUser", mapper.mapAppUserToAppUserDto(appUser));
        return "profile/profile";
    }

    @GetMapping("/edit")
    public String showEditProfileForm(Model model, @AuthenticationPrincipal AppUser principal) {
        AppUser appUser = userService.findById(principal.getId());
        model.addAttribute("appUser", mapper.mapAppUserToAppUserDto(appUser));
        return "profile/editProfile";
    }

    @PostMapping("/edit")
    public String editProfile(@ModelAttribute("appUser")
                                  @Valid
                              AppUserDto appUserDto) {
        userService.updateUserProfile(appUserDto);
        return "redirect:/profile";
    }
}
