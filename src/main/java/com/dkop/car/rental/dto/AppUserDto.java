package com.dkop.car.rental.dto;

import com.dkop.car.rental.model.user.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDto {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private boolean isActive;
}