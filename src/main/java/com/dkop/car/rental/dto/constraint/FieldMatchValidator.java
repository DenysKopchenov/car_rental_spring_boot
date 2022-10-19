package com.dkop.car.rental.dto.constraint;

import com.dkop.car.rental.dto.RegFormDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, RegFormDto> {

    @Override
    public boolean isValid(RegFormDto regFormDto, ConstraintValidatorContext context) {
        return Objects.nonNull(regFormDto.getPassword()) && regFormDto.getPassword().equals(regFormDto.getConfPassword());
    }
}
