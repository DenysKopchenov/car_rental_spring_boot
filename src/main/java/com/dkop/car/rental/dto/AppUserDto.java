package com.dkop.car.rental.dto;

import com.dkop.car.rental.model.user.Role;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@Data
public class AppUserDto {

    private static final String NAME_REGEX = "[A-Z_А-ЯЇІЄҐ][a-z_а-яїієґ']{2,20}";
    private static final String NAME_VALIDATION_MESSAGE = "{validation.name}";
    private static final String EMAIL_VALIDATION_MESSAGE = "{validation.email}";

    private UUID id;
    @NotBlank
    @Email(message = EMAIL_VALIDATION_MESSAGE)
    private String email;
    @NotBlank
    @Pattern(regexp = NAME_REGEX, message = NAME_VALIDATION_MESSAGE)
    private String firstName;
    @NotBlank
    @Pattern(regexp = NAME_REGEX, message = NAME_VALIDATION_MESSAGE)
    private String lastName;
    private Role role;
    private boolean isActive;
}