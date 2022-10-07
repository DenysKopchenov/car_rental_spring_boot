package com.dkop.car.rental.service.impl;

import com.dkop.car.rental.dto.RegFormDto;
import com.dkop.car.rental.exception.UserAlreadyExists;
import com.dkop.car.rental.model.client.AppUser;
import com.dkop.car.rental.model.client.Role;
import com.dkop.car.rental.repository.AppUserRepository;
import com.dkop.car.rental.service.UserService;
import com.dkop.car.rental.util.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final Mapper mapper;

    @Autowired
    public UserServiceImpl(AppUserRepository appUserRepository, @Lazy PasswordEncoder passwordEncoder, Mapper mapper) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }


    @Override
    public AppUser savePerson(RegFormDto regFormDto) throws UserAlreadyExists {
        checkIsUserExists(regFormDto);
        AppUser appUser = mapper.mapUserDtoToUser(regFormDto);
        appUser.setEncodedPassword(passwordEncoder.encode(regFormDto.getPassword()));
        return appUserRepository.save(appUser);
    }

    @Override
    public AppUser saveManager(RegFormDto regFormDto) throws UserAlreadyExists {
        checkIsUserExists(regFormDto);

        AppUser appUser = mapper.mapUserDtoToUser(regFormDto);
        appUser.setEncodedPassword(passwordEncoder.encode(regFormDto.getPassword()));
        appUser.setRole(Role.MANAGER);
        return appUserRepository.save(appUser);
    }

    private void checkIsUserExists(RegFormDto regFormDto) throws UserAlreadyExists {
        if (appUserRepository.existsByEmail(regFormDto.getEmail())) {
            throw new UserAlreadyExists(regFormDto.getEmail() + " already exist");
        }
    }

    @Override
    public AppUser findByEmail(String email) {
        return appUserRepository.findByEmail(email).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(username).orElseThrow(EntityNotFoundException::new);
    }
}
