package com.example.user.services;

import com.example.user.dtos.CashUserDTO;
import com.example.user.exceptions.CashUserNotFoundException;
import com.example.user.model.CashUser;
import com.example.user.repositories.CashUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CashUserServiceImpl implements CashUserService{


    private final CashUserRepository cashUserRepository;
    private final ModelMapper modelMapper;

    public CashUserServiceImpl(CashUserRepository cashUserRepository,
                               ModelMapper modelMapper ){

        this.cashUserRepository = cashUserRepository;
        this.modelMapper = modelMapper;
        log.info("AccountsServiceImpl instantiated.");
    }

    @Override
    public CashUserDTO getByName(String name){

        CashUser user = cashUserRepository.findByName(name).get(0);
        if (user == null){
            throw new RuntimeException("User not found!");
        }
        return modelMapper.map(user, CashUserDTO.class);
    }

    @Override
    public List<CashUserDTO> getAll(){
        return cashUserRepository.findAll().stream().map(cashUser -> modelMapper.map(cashUser, CashUserDTO.class)).collect(Collectors.toList());
    }

    @Override
    public CashUserDTO createAccount(CashUserDTO cashUserDTO) {


        if(!cashUserRepository.findByName(cashUserDTO.getName()).isEmpty()){
            throw new CashUserNotFoundException("The user with name " + cashUserDTO.getName() + " already exists.");
        }

        CashUser user = CashUser.builder()
                .name(cashUserDTO.getName())
                .password(cashUserDTO.getPassword())
                .build();

        cashUserRepository.save(user);

        return cashUserDTO;
    }


}
