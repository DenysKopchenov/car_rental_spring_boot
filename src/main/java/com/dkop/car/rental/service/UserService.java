package com.dkop.car.rental.service;

import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.dto.RegFormDto;
import com.dkop.car.rental.exception.UserAlreadyExists;
import com.dkop.car.rental.model.client.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserService extends UserDetailsService {

    AppUser savePerson(RegFormDto regFormDto) throws UserAlreadyExists;

    AppUser saveManager(RegFormDto regFormDto) throws UserAlreadyExists;

    AppUser findByEmail(String email);

    Page<AppUser> findAllWithRoleUser(PaginationAndSortingBean paginationAndSortingBean);

    AppUser changeUserIsActive(Boolean isActive, UUID id);
}
