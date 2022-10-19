package com.dkop.car.rental.dto;

import com.dkop.car.rental.model.user.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDto {

    private static final String NAME_REGEX = "[A-Za-z]{2,20}";

    private UUID id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = NAME_REGEX)
    private String firstName;
    @NotBlank
    @Pattern(regexp = NAME_REGEX)
    private String lastName;
    private Role role;
    private boolean isActive;
}