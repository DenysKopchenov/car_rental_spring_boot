package com.dkop.car.rental.dto;

import com.dkop.car.rental.model.client.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * A DTO for the {@link com.dkop.car.rental.model.client.AppUser} entity
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDto implements Serializable {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private boolean isActive;
}