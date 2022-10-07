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

    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,20}$")
    private String password;
    @NotBlank
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,20}$")
    private String confPassword;
    @NotBlank
    @Pattern(regexp = "[A-Za-z]{2,20}")
    private String firstName;
    @NotBlank
    @Pattern(regexp = "[A-Za-z]{2,20}")
    private String lastName;
    @NotNull
    @AssertTrue
    private boolean termsOfUse;
}
