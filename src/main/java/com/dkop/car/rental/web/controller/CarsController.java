package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.dto.CarDto;
import com.dkop.car.rental.dto.CarFilterBean;
import com.dkop.car.rental.dto.PaginationAndSortingBean;
import com.dkop.car.rental.model.car.Car;
import com.dkop.car.rental.model.car.CategoryClass;
import com.dkop.car.rental.model.car.Manufacturer;
import com.dkop.car.rental.model.user.Role;
import com.dkop.car.rental.service.CarService;
import com.dkop.car.rental.util.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cars")
@Slf4j
public class CarsController {

    private static final String TITLE_ATTRIBUTE = "title";
    private static final String CARS_TITLE = "Cars";

    private final CarService carService;
    private final Mapper mapper;

    public CarsController(CarService carService, Mapper mapper) {
        this.carService = carService;
        this.mapper = mapper;
    }

    @GetMapping
    public String showCarList(Model model,
                              @ModelAttribute("pagination") PaginationAndSortingBean paginationAndSortingBean,
                              @ModelAttribute("filter") CarFilterBean carFilterBean) {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        Page<Car> pagedCars;
        if (authorities.contains(Role.ADMIN)) {
            pagedCars = carService.findAll(paginationAndSortingBean, carFilterBean);
        } else {
            pagedCars = carService.findAllAvailable(paginationAndSortingBean, carFilterBean);
        }
        List<CarDto> cars = pagedCars.map(mapper::mapCarToCarDto)
                .toList();

        model.addAttribute(TITLE_ATTRIBUTE, CARS_TITLE);
        model.addAttribute("cars", cars);
        model.addAttribute("numberOfPages", pagedCars.getTotalPages());
        setManufacturersAndCategoryClassAttributes(model);
        return "cars/cars";
    }

    @GetMapping("/image")
    public void showImage(@RequestParam("id") UUID id, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif, image/pdf");
        response.getOutputStream().write(carService.findImageByCarId(id));
        response.getOutputStream().close();
    }

    @GetMapping("/{id}")
    public String showCar(@PathVariable("id") UUID id, Model model) {
        Car car = carService.findById(id);
        CarDto carDto = mapper.mapCarToCarDto(car);
        model.addAttribute(TITLE_ATTRIBUTE, car.getModel());
        model.addAttribute("car", carDto);
        return "cars/carInfo";
    }

    private void setManufacturersAndCategoryClassAttributes(Model model) {
        model.addAttribute("manufacturers", Arrays.stream(Manufacturer.values()).collect(Collectors.toList()));
        model.addAttribute("class", Arrays.stream(CategoryClass.values()).collect(Collectors.toList()));
    }
}
