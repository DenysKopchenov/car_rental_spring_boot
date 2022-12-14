package com.dkop.car.rental.service.impl;

import com.dkop.car.rental.dto.AppUserDto;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.dto.RegFormDto;
import com.dkop.car.rental.exception.UserAlreadyExistsException;
import com.dkop.car.rental.model.user.AppUser;
import com.dkop.car.rental.model.user.Role;
import com.dkop.car.rental.repository.AppUserRepository;
import com.dkop.car.rental.service.MailService;
import com.dkop.car.rental.service.UserService;
import com.dkop.car.rental.util.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_USER_SORT = "email";
    private static final String USER_ALREADY_EXIST_PATTERN = "%s already exists";
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final Mapper mapper;
    private final MailService mailService;

    @Autowired
    public UserServiceImpl(AppUserRepository appUserRepository,
                           @Lazy PasswordEncoder passwordEncoder,
                           Mapper mapper,
                           MailService mailService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.mailService = mailService;
    }


    @Override
    @Transactional
    public AppUser saveAppUser(RegFormDto regFormDto) throws UserAlreadyExistsException {
        checkIsUserExists(regFormDto.getEmail());

        AppUser appUser = mapper.mapRegFormDtoToUser(regFormDto);
        appUser.setEncodedPassword(passwordEncoder.encode(regFormDto.getPassword()));
        AppUser savedUser = appUserRepository.save(appUser);
        mailService.sendEmail(savedUser.getEmail(), "Registration completed",
                String.format("You account %s successfully registered in our car rental service.", savedUser.getEmail()));
        return savedUser;
    }

    @Override
    @Transactional
    public AppUser saveManager(RegFormDto regFormDto) throws UserAlreadyExistsException {
        checkIsUserExists(regFormDto.getEmail());

        AppUser appUser = mapper.mapRegFormDtoToUser(regFormDto);
        appUser.setEncodedPassword(passwordEncoder.encode(regFormDto.getPassword()));
        appUser.setRole(Role.MANAGER);
        return appUserRepository.save(appUser);
    }

    @Override
    public AppUser findById(UUID id) {
        return appUserRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("findById() Invalid user id"));
    }

    @Override
    public Page<AppUser> findAllByRole(PaginationAndSortingBean pagination, Role role) {
        if (Objects.isNull(pagination.getSort())) {
            pagination.setSort(DEFAULT_USER_SORT);
        }
        PageRequest of = PageRequest.of(pagination.getPage() - 1, pagination.getSize(),
                Sort.by(Sort.Direction.valueOf(pagination.getDirection()), pagination.getSort()));
        return appUserRepository.findByRoleIn(List.of(role), of);
    }

    @Override
    @Transactional
    public AppUser changeUserActiveStatus(boolean isActive, UUID id) {
        AppUser appUser = findById(id);
        appUser.setActive(isActive);
        return appUserRepository.save(appUser);
    }

    @Override
    @Transactional
    public AppUser updateUserProfile(AppUserDto appUserDto) throws UserAlreadyExistsException {
        AppUser appUser = findById(appUserDto.getId());
        String userInputEmail = appUserDto.getEmail();
        if (!appUser.getEmail().equals(userInputEmail)) {
            checkIsUserExists(userInputEmail);
        }
        appUser.setEmail(userInputEmail);
        appUser.setFirstName(appUserDto.getFirstName());
        appUser.setLastName(appUserDto.getLastName());
        return appUserRepository.save(appUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found", username)));
    }

    private void checkIsUserExists(String email) throws UserAlreadyExistsException {
        if (appUserRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(String.format(USER_ALREADY_EXIST_PATTERN, (email)));
        }
    }
}
