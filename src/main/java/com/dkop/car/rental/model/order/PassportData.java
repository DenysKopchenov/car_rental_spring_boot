package com.dkop.car.rental.model.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PassportData {

    private static final String NAME_REGEX = "[A-Z_А-ЯЇІЄҐ][a-z_а-яїієґ']{2,20}";
    @Id
    @GeneratedValue()
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    @NotBlank
    @Pattern(regexp = NAME_REGEX)
    private String firstName;
    @NotBlank
    @Pattern(regexp = NAME_REGEX)
    private String lastName;
    @NotBlank
    private String passportCode;
    @NotNull
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate issueDate;
    @NotBlank
    private String issueDepartment;
}
