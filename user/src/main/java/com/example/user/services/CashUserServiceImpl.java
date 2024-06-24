package com.example.user.services;

import com.example.user.dtos.CashUserDTO;
import com.example.user.exceptions.CashUserNotFoundException;
import com.example.user.model.CashUser;
import com.example.user.repositories.CashUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
    public List<CashUserDTO> getAll(){
        return cashUserRepository.findAll().stream().map(cashUser -> modelMapper.map(cashUser, CashUserDTO.class)).collect(Collectors.toList());
    }

    @Override
    public CashUserDTO getByName(String name){

        Optional<CashUser> user = cashUserRepository.findByName(name);
        if (user.isEmpty()){
            throw new CashUserNotFoundException("The user with name " + name + " not found.");
        }
        return modelMapper.map(user, CashUserDTO.class);
    }

    @Override
    public CashUserDTO getById(Long id){
        CashUser user = cashUserRepository.getReferenceById(id);
        return modelMapper.map(user, CashUserDTO.class);
    }
    @Override
    public CashUserDTO createAccount(CashUserDTO cashUserDTO) {

        Optional<CashUser> optUser = cashUserRepository.findByName(cashUserDTO.getName());
        if(optUser.isPresent()){
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
