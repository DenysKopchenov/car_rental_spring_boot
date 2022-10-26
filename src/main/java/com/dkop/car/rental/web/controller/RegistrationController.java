package com.dkop.car.rental.web.controller;

import com.dkop.car.rental.dto.ReCaptchaResponse;
import com.dkop.car.rental.dto.RegFormDto;
import com.dkop.car.rental.exception.UserAlreadyExists;
import com.dkop.car.rental.model.user.AppUser;
import com.dkop.car.rental.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Collections;

@Controller
@RequestMapping("/registration")
@Slf4j
public class RegistrationController {

    private static final String REGISTRATION_PAGE = "registration";
    private static final String USER = "appUser";
    private static final String TITLE_ATTRIBUTE = "title";
    private static final String TITLE_VALUE = "Registration";
    private static final String SUCCESS_REGISTRATION = "redirect:/registration?success";
    private static final String FAILED_REGISTRATION = "redirect:/registration?failed";

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;
    @Value("${recaptcha.url}")
    private String recaptchaServerUrl;
    private final UserService userService;
    private final RestTemplate restTemplate;


    @Autowired
    public RegistrationController(UserService userService, @Lazy RestTemplate restTemplate) {
        this.userService = userService;
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public String showRegistrationForm(@ModelAttribute(USER) RegFormDto regFormDto, Model model) {
        model.addAttribute(TITLE_ATTRIBUTE, TITLE_VALUE);
        return REGISTRATION_PAGE;
    }


    @PostMapping
    public String register(@ModelAttribute(USER) @Valid RegFormDto regFormDto,
                           BindingResult bindingResult,
                           @RequestParam("g-recaptcha-response") String gRecaptchaResponse,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (!verifyReCaptcha(gRecaptchaResponse)) {
            model.addAttribute("captcha", "First validate captcha");
            log.info("{} captcha error", regFormDto.getEmail());
            return REGISTRATION_PAGE;
        }
        if (bindingResult.hasErrors()) {
            return REGISTRATION_PAGE;
        }

        try {
            AppUser appUser = userService.saveAppUser(regFormDto);
            log.info("{} successfully registered", appUser.getEmail());
            return SUCCESS_REGISTRATION;
        } catch (UserAlreadyExists ex) {
            redirectAttributes.addFlashAttribute(USER, regFormDto);
            return FAILED_REGISTRATION;
        }
    }

    private boolean verifyReCaptcha(String gRecaptchaResponse) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("secret", Collections.singletonList(recaptchaSecret));
        map.put("response", Collections.singletonList(gRecaptchaResponse));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, httpHeaders);

        ReCaptchaResponse response = restTemplate.postForObject(recaptchaServerUrl, request, ReCaptchaResponse.class);
        return response.isSuccess();
    }
}
