package com.example.user.controller;

import com.example.user.dtos.CashUserDTO;
import com.example.user.exceptions.CashUserNotFoundException;
import com.example.user.services.CashUserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Slf4j
@RequestMapping("/user")
public class CashUserController {
    private final CashUserService cashUserService;

    public CashUserController(CashUserService cashUserService) {
        this.cashUserService = cashUserService;
    }


    @GetMapping(value="/list")
    public CollectionModel<CashUserDTO> findAll(@RequestHeader("cashmate-id")
                                                    String correlationId) {

        List<CashUserDTO> users = cashUserService.getAll();
        for(CashUserDTO user: users){
            Link selfLink = linkTo(methodOn(CashUserController.class)
                    .findById(correlationId, user.getId())).withSelfRel();
            user.add(selfLink);
        }

        Link link = linkTo(methodOn(CashUserController.class).findAll(correlationId)).withSelfRel();
        CollectionModel<CashUserDTO> result = CollectionModel.of(users, link);
        return result;
    }

    @GetMapping("/{id}")
    public CashUserDTO findById(@RequestHeader("cashmate-id")
                                    String correlationId, @PathVariable Long id){
        CashUserDTO user = cashUserService.getById(id);
        Link selfLink = linkTo(methodOn(CashUserController.class)
                .findById(correlationId, user.getId())).withSelfRel();

        user.add(selfLink);

        return user;
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