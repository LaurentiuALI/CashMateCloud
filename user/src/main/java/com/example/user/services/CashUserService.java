package com.example.user.services;

import com.example.user.dtos.CashUserDTO;

import java.util.List;

public interface CashUserService {

    CashUserDTO getByName(String name);
    CashUserDTO createAccount(CashUserDTO cashUserDTO);
    List<CashUserDTO> getAll();
}
