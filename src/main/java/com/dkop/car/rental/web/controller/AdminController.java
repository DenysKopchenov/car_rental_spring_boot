package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.dto.AppUserDto;
import com.dkop.car.rental.dto.CarDto;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.dto.RegFormDto;
import com.dkop.car.rental.exception.UserAlreadyExists;
import com.dkop.car.rental.model.car.Car;
import com.dkop.car.rental.model.car.CategoryClass;
import com.dkop.car.rental.model.car.Manufacturer;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private static final String USER = "appUser";
    private static final String TITLE_ATTRIBUTE = "title";
    private static final String TITLE_REGISTER_NEW_MANAGER = "Register new manager";
    private static final String NEW_CAR_TITLE = "Add new car";

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
    public String showAdminPage(@ModelAttribute(USER) RegFormDto regFormDto, Model model) {
        model.addAttribute(TITLE_ATTRIBUTE, TITLE_REGISTER_NEW_MANAGER);
        return "admin/admin";
    }

    @GetMapping("/newManager")
    public String showNewManagerRegistrationForm(@ModelAttribute(USER) RegFormDto regFormDto, Model model) {
        model.addAttribute(TITLE_ATTRIBUTE, TITLE_REGISTER_NEW_MANAGER);
        return REGISTRATION_PAGE;
    }

    @PostMapping("/newManager")
    public String registerNewManager(@ModelAttribute(USER) @Valid RegFormDto regFormDto,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return REGISTRATION_PAGE;
        }
        try {
            userService.saveAppUser(regFormDto);
            return "redirect:/registration/manager?success";
        } catch (UserAlreadyExists ex) {
            redirectAttributes.addFlashAttribute(USER, regFormDto);
            return "redirect:/registration/manager?failed";
        }
    }

    @GetMapping("/newCar")
    public String showNewCarForm(@ModelAttribute("car") CarDto carDto, Model model) {
        model.addAttribute(TITLE_ATTRIBUTE, NEW_CAR_TITLE);
        setManufacturersAndCategoryClassAttributes(model);
        return "cars/newCar";
    }

    @SneakyThrows
    @PostMapping("/newCar")
    public String createNewCar(@ModelAttribute("car") CarDto carDto, Model model,
                               @RequestParam("image") MultipartFile multipartImage) {
        Car car = carService.saveCar(carDto);
        return "redirect:/cars/" + car.getId() + "?success";
    }

    @DeleteMapping("/deleteCar/{id}")
    public String deleteCar(@PathVariable("id") UUID id) {
        carService.deleteCar(id);
        return "redirect:/cars";
    }

    @GetMapping("/editCar/{id}")
    public String showEditCarForm(@PathVariable("id") UUID id, Model model) {
        model.addAttribute("updated", carService.findById(id));
        setManufacturersAndCategoryClassAttributes(model);
        return "cars/editCar";
    }

    @PutMapping("/editCar/{id}")
    public String editCar(@ModelAttribute("updated") CarDto updated, Model model,
                          @RequestParam("image") MultipartFile multipartImage) {
        carService.updateCar(updated);
        return "redirect:/cars/{id}?success";
    }

    @GetMapping("/showUsers")
    public String showUsers(@ModelAttribute("pagination") PaginationAndSortingBean paginationAndSortingBean, Model model) {
        Page<AppUser> allUserPage = userService.findAllByRole(paginationAndSortingBean, Role.USER);
        List<AppUserDto> allUsers = allUserPage.stream()
                .map(mapper::mapAppUserToAppUserDto)
                .collect(Collectors.toList());
        model.addAttribute("numberOfPages", allUserPage.getTotalPages());
        model.addAttribute("users", allUsers);
        return "admin/users";
    }

    @PutMapping("/blockUser/{id}")
    public String blockUser(@PathVariable("id") UUID id, RedirectAttributes redirectAttributes) {
        AppUser appUser = userService.changeUserIsActive(Boolean.FALSE, id);
        redirectAttributes.addFlashAttribute("userResult", mapper.mapAppUserToAppUserDto(appUser));
        return "redirect:/admin/showUsers?blocked";
    }

    @PutMapping("/unblockUser/{id}")
    public String unblockUser(@PathVariable("id") UUID id, RedirectAttributes redirectAttributes) {
        AppUser appUser = userService.changeUserIsActive(Boolean.TRUE, id);
        redirectAttributes.addFlashAttribute("userResult", mapper.mapAppUserToAppUserDto(appUser));
        return "redirect:/admin/showUsers?unblocked";
    }


    private static void setManufacturersAndCategoryClassAttributes(Model model) {
        model.addAttribute("manufacturers", Arrays.stream(Manufacturer.values()).collect(Collectors.toList()));
        model.addAttribute("class", Arrays.stream(CategoryClass.values()).collect(Collectors.toList()));
    }
}
