package com.example.user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CashUserController {
    @GetMapping("/test")
    public String getTest(){
        return "Testing";
    }
}