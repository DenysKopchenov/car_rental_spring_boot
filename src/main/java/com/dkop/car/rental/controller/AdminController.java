package com.dkop.car.rental.controller;

import com.dkop.car.rental.dto.CarDto;
import com.dkop.car.rental.dto.RegFormDto;
import com.dkop.car.rental.exception.UserAlreadyExists;
import com.dkop.car.rental.model.car.CategoryClass;
import com.dkop.car.rental.model.car.Manufacturer;
import com.dkop.car.rental.service.CarService;
import com.dkop.car.rental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Arrays;
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

    @Autowired
    public AdminController(CarService carService, UserService userService) {
        this.carService = carService;
        this.userService = userService;
    }


    @GetMapping
    public String showAdminPAge(@ModelAttribute(USER) RegFormDto regFormDto, Model model) {
        model.addAttribute(TITLE_ATTRIBUTE, TITLE_REGISTER_NEW_MANAGER);
        return "admin/admin";
    }

    @GetMapping("/newManager")
    public String showManagerRegistrationForm(@ModelAttribute(USER) RegFormDto regFormDto, Model model) {
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
            userService.savePerson(regFormDto);
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

    @PostMapping("/newCar")
    public String createNewCar(@ModelAttribute("car") CarDto carDto) {
        carService.saveCar(carDto);
        return "redirect:/cars?success";
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
    public String editCar(@ModelAttribute("updated") CarDto updated, Model model) {
        carService.updateCar(updated);
        return "redirect:/cars/{id}";
    }

    private static void setManufacturersAndCategoryClassAttributes(Model model) {
        model.addAttribute("manufacturers", Arrays.stream(Manufacturer.values()).collect(Collectors.toList()));
        model.addAttribute("class", Arrays.stream(CategoryClass.values()).collect(Collectors.toList()));
    }
}
