package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.dto.AppUserDto;
import com.dkop.car.rental.dto.CarDto;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.dto.RegFormDto;
import com.dkop.car.rental.exception.UserAlreadyExists;
import com.dkop.car.rental.model.car.Car;
import com.dkop.car.rental.model.car.CategoryClass;
import com.dkop.car.rental.model.car.Fuel;
import com.dkop.car.rental.model.car.Manufacturer;
import com.dkop.car.rental.model.car.Transmission;
import com.dkop.car.rental.model.user.AppUser;
import com.dkop.car.rental.model.user.Role;
import com.dkop.car.rental.service.CarService;
import com.dkop.car.rental.service.UserService;
import com.dkop.car.rental.util.Mapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private static final String REGISTRATION_PAGE = "registration";
    private static final String TITLE_ATTRIBUTE = "title";
    private static final String ADMIN_PAGE = "title";
    private static final String REGISTER_NEW_MANAGER_TITLE = "Register new manager";
    private static final String ADD_NEW_CAR_TITLE = "Add new car";
    private static final String USER = "appUser";
    private static final String MANUFACTURERS_ATTRIBUTE = "manufacturers";
    private static final String CATEGORIES_ATTRIBUTE = "class";
    private static final String FUEL_TYPES_ATTRIBUTE = "fuelTypes";
    private static final String TRANSMISSION_TYPES_ATTRIBUTE = "transmissionTypes";

    private final CarService carService;
    private final UserService userService;
    private final Mapper mapper;

    @Autowired
    public AdminController(CarService carService, UserService userService, Mapper mapper) {
        this.carService = carService;
        this.userService = userService;
        this.mapper = mapper;
    }


    @GetMapping
    public String showAdminPage(Model model) {
        model.addAttribute(TITLE_ATTRIBUTE, ADMIN_PAGE);
        return "admin/admin";
    }

    @GetMapping("/new/manager")
    public String showNewManagerRegistrationForm(@ModelAttribute(USER) RegFormDto regFormDto, Model model) {
        model.addAttribute(TITLE_ATTRIBUTE, REGISTER_NEW_MANAGER_TITLE);
        return REGISTRATION_PAGE;
    }

    @PostMapping("/new/manager")
    public String registerNewManager(@ModelAttribute(USER) @Valid RegFormDto regFormDto,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return REGISTRATION_PAGE;
        }
        try {
            userService.saveManager(regFormDto);
            return "redirect:/registration/manager?success";
        } catch (UserAlreadyExists ex) {
            redirectAttributes.addFlashAttribute(USER, regFormDto);
            return "redirect:/registration/manager?failed";
        }
    }

    @GetMapping("/new/car")
    public String showNewCarForm(@ModelAttribute("car") CarDto carDto, Model model) {
        model.addAttribute(TITLE_ATTRIBUTE, ADD_NEW_CAR_TITLE);
        setManufacturersAndCategoryClassAttributes(model);
        return "admin/newCar";
    }

    @SneakyThrows
    @PostMapping("/new/car")
    public String createNewCar(@ModelAttribute("car")
                               @Valid CarDto carDto,
                               RedirectAttributes redirectAttributes,
                               @RequestParam("image") MultipartFile multipartImage) {
        if (multipartImage.getBytes().length == 0) {
            redirectAttributes.addFlashAttribute("noImage", "Cant create car w/o image!");
            redirectAttributes.addFlashAttribute("car", carDto);
            return "redirect:/admin/newCar";
        }
        Car car = carService.saveCar(carDto);
        return "redirect:/cars/" + car.getId() + "?success";
    }

    @PutMapping("/car/unavailable/{id}")
    public String setUnavailable(@PathVariable("id") UUID id, HttpServletRequest request) {
        carService.setAvailability(id, Boolean.FALSE);
        return "redirect:" + request.getHeader("referer");
    }

    @PutMapping("/car/available/{id}")
    public String setAvailable(@PathVariable("id") UUID id, HttpServletRequest request) {
        carService.setAvailability(id, Boolean.TRUE);
        return "redirect:" + request.getHeader("referer");
    }

    @GetMapping("/edit/car/{id}")
    public String showEditCarForm(@PathVariable("id") UUID id, Model model) {
        Car car = carService.findById(id);
        model.addAttribute("updated", mapper.mapCarToCarDto(car));
        setManufacturersAndCategoryClassAttributes(model);
        return "admin/editCar";
    }

    @PutMapping("/edit/car/{id}")
    public String editCar(@ModelAttribute("updated") @Valid CarDto updated,
                          @RequestParam("image") MultipartFile multipartImage) {
        carService.updateCar(updated);
        return "redirect:/cars/{id}?success";
    }

    @GetMapping("/users")
    public String showUsers(@ModelAttribute("pagination") PaginationAndSortingBean paginationAndSortingBean, Model model) {
        Page<AppUser> allUserPage = userService.findAllByRole(paginationAndSortingBean, Role.USER);
        List<AppUserDto> allUsers = allUserPage.stream()
                .map(mapper::mapAppUserToAppUserDto)
                .collect(Collectors.toList());
        model.addAttribute("numberOfPages", allUserPage.getTotalPages());
        model.addAttribute("users", allUsers);
        return "admin/users";
    }

    @PutMapping("/block/{id}")
    public String blockUser(@PathVariable("id") UUID id, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        AppUser appUser = userService.changeUserActiveStatus(Boolean.FALSE, id);
        redirectAttributes.addFlashAttribute("userResult", mapper.mapAppUserToAppUserDto(appUser));
        return "redirect:" + request.getHeader("referer");
    }

    @PutMapping("/unblock/{id}")
    public String unblockUser(@PathVariable("id") UUID id, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        AppUser appUser = userService.changeUserActiveStatus(Boolean.TRUE, id);
        redirectAttributes.addFlashAttribute("userResult", mapper.mapAppUserToAppUserDto(appUser));
        return "redirect:" + request.getHeader("referer");
    }


    private static void setManufacturersAndCategoryClassAttributes(Model model) {
        model.addAttribute(MANUFACTURERS_ATTRIBUTE, Arrays.stream(Manufacturer.values()).collect(Collectors.toList()));
        model.addAttribute(CATEGORIES_ATTRIBUTE, Arrays.stream(CategoryClass.values()).collect(Collectors.toList()));
        model.addAttribute(FUEL_TYPES_ATTRIBUTE, Arrays.stream(Fuel.values()).collect(Collectors.toList()));
        model.addAttribute(TRANSMISSION_TYPES_ATTRIBUTE, Arrays.stream(Transmission.values()).collect(Collectors.toList()));
    }
}
