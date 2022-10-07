package com.dkop.car.rental.dto;

import com.dkop.car.rental.model.client.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * A DTO for the {@link com.dkop.car.rental.model.client.AppUser} entity
 */
@Getter
@Setter
@RequiredArgsConstructor
public class AppUserDto implements Serializable {
    private final UUID id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final Role role;
    private final boolean isActive;
}