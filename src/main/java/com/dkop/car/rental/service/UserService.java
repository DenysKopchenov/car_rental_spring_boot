package com.dkop.car.rental.service;

import com.dkop.car.rental.dto.AppUserDto;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.dto.RegFormDto;
import com.dkop.car.rental.exception.UserAlreadyExists;
import com.dkop.car.rental.model.user.AppUser;
import com.dkop.car.rental.model.user.Role;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserService extends UserDetailsService {

    AppUser saveAppUser(RegFormDto regFormDto) throws UserAlreadyExists;

    AppUser saveManager(RegFormDto regFormDto) throws UserAlreadyExists;

    AppUser findByEmail(String email);

    AppUser findById(UUID id);

    Page<AppUser> findAllByRole(PaginationAndSortingBean paginationAndSortingBean, Role role);

    AppUser changeUserActiveStatus(Boolean isActive, UUID id);

    AppUser updateUserProfile(AppUserDto appUserDto);
}
