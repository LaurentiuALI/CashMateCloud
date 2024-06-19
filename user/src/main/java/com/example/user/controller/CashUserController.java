package com.example.user.controller;

import com.example.user.dtos.CashUserDTO;
import com.example.user.exceptions.CashUserNotFoundException;
import com.example.user.services.CashUserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class CashUserController {
    private final CashUserService cashUserService;

    public CashUserController(CashUserService cashUserService) {
        this.cashUserService = cashUserService;
    }


    @GetMapping("/users")
    public List<CashUserDTO> getUsers(){
        return cashUserService.getAll();
    }


    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody CashUserDTO user, BindingResult bindingResult) {
        log.info("Entering register method");

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            errors.forEach(log::error);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            log.info(user.toString());
            cashUserService.createAccount(user);
            log.info("User registered successfully.");
            return new ResponseEntity<>("User registered successfully\n" + user, HttpStatus.OK);
        } catch (CashUserNotFoundException ex) {
            log.error("User registration failed: {}", ex.getMessage());
            return new ResponseEntity<>(new String[]{ex.getMessage()}, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            log.error("An unexpected error occurred: {}", ex.getMessage());
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/test")
    public String getTest(){
        return "Testing";
    }
}