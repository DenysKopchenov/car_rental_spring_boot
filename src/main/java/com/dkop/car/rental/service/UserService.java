package com.dkop.car.rental.service;

import com.dkop.car.rental.dto.RegFormDto;
import com.dkop.car.rental.exception.UserAlreadyExists;
import com.dkop.car.rental.model.client.AppUser;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    AppUser savePerson(RegFormDto regFormDto) throws UserAlreadyExists;

    AppUser saveManager(RegFormDto regFormDto) throws UserAlreadyExists;

    AppUser findByEmail(String email);
}
