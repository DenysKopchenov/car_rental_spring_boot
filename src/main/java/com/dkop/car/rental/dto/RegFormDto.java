package com.dkop.car.rental.dto;

import com.dkop.car.rental.dto.constraint.PasswordMatch;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@PasswordMatch(message = "{validation.password.confirm}")
public class RegFormDto {

    private static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,20}$";
    private static final String NAME_REGEX = "[A-Z_А-ЯЇІЄҐ][a-z_а-яїієґ']{2,20}";
    private static final String PASSWORD_VALIDATION_MESSAGE = "{validation.password}";
    private static final String NAME_VALIDATION_MESSAGE = "{validation.name}";
    private static final String EMAIL_VALIDATION_MESSAGE = "{validation.email}";
    private static final String TERMS_OF_USE_VALIDATION_MESSAGE = "{validation.terms.of.use}";

    @NotBlank
    @Email(message = EMAIL_VALIDATION_MESSAGE)
    private String email;
    @NotBlank
    @Pattern(regexp = PASSWORD_REGEX, message = PASSWORD_VALIDATION_MESSAGE)
    private String password;
    @NotBlank
    @Pattern(regexp = PASSWORD_REGEX, message = PASSWORD_VALIDATION_MESSAGE)
    private String confPassword;
    @NotBlank
    @Pattern(regexp = NAME_REGEX, message = NAME_VALIDATION_MESSAGE)
    private String firstName;
    @NotBlank
    @Pattern(regexp = NAME_REGEX, message = NAME_VALIDATION_MESSAGE)
    private String lastName;
    @NotNull
    @AssertTrue(message = TERMS_OF_USE_VALIDATION_MESSAGE)
    private boolean termsOfUse;
}
