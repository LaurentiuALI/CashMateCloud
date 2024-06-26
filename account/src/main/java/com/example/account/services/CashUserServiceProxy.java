package com.example.account.services;

import com.example.account.dtos.CashUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value="user-service")
public interface CashUserServiceProxy {
    @GetMapping("/user/{id}")
     CashUserDTO findById(@RequestHeader("cashmate-id")
                          String correlationId, @PathVariable Long id);
}
