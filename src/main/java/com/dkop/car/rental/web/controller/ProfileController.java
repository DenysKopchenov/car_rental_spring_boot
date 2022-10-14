package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.dto.AppUserDto;
import com.dkop.car.rental.model.user.AppUser;
import com.dkop.car.rental.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
@PreAuthorize("hasAuthority('USER')")
public class ProfileController {

    private UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showProfile() {
        return "profile/profile";
    }

    @GetMapping("/edit")
    public String editProfile(@ModelAttribute("appUser")
                              AppUserDto appUserDto, @AuthenticationPrincipal AppUser principal) {
        appUserDto.setFirstName(principal.getFirstName());
        appUserDto.setLastName(principal.getLastName());
        appUserDto.setEmail(principal.getEmail());
        return "profile/editProfile";
    }
}
