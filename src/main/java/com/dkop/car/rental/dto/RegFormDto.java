package com.dkop.car.rental.dto;

import com.dkop.car.rental.constraint.FieldMatch;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@FieldMatch
public class RegFormDto {

    private static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,20}$";
    private static final String NAME_REGEX = "[A-Za-z]{2,20}";

    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = PASSWORD_REGEX)
    private String password;
    @NotBlank
    @Pattern(regexp = PASSWORD_REGEX)
    private String confPassword;
    @NotBlank
    @Pattern(regexp = NAME_REGEX)
    private String firstName;
    @NotBlank
    @Pattern(regexp = NAME_REGEX)
    private String lastName;
    @NotNull
    @AssertTrue
    private boolean termsOfUse;
}
