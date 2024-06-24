package com.example.account.controller;

import com.example.account.dtos.AccountDTO;
import com.example.account.dtos.CashUserDTO;
import com.example.account.exceptions.CashUserNotFoundException;
import com.example.account.exceptions.ResourceNotFoundException;
import com.example.account.services.AccountsService;
import com.example.account.services.CashUserServiceProxy;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/accounts")
public class AccountController {

    private final AccountsService accountsService;
    private final CashUserServiceProxy cashUserServiceProxy;

    @Autowired
    public AccountController(AccountsService accountsService, CashUserServiceProxy cashUserServiceProxy) {
        this.accountsService = accountsService;
        this.cashUserServiceProxy = cashUserServiceProxy;
    }

    @GetMapping("/list")
    public List<EntityModel<AccountDTO>> findAll() {
        return new ArrayList<>(accountsService.getAll().stream()
                .map(account -> toModel(account, account.getUser_id()))
                .toList());
    }

    @PostMapping("/add/{userID}")
    public ResponseEntity<Object> createAccount(@RequestBody AccountDTO account, @PathVariable Long userID) {
        CashUserDTO user = cashUserServiceProxy.findById(userID);
        if (user == null) {
            return new ResponseEntity<>("No user found.", HttpStatus.BAD_REQUEST);
        } else {
            AccountDTO createdAccount = accountsService.createAccount(account, userID);
            EntityModel<AccountDTO> model = toModel(createdAccount, userID);
            return new ResponseEntity<>(model, HttpStatus.OK);
        }
    }

    @PostMapping("/addMember/{accountId}/{userID}")
    public ResponseEntity<Object> addMemberToAccount(@PathVariable Long accountId, @PathVariable Long userID){
        try{
            accountsService.addAccountMember(accountId, userID);
            return new ResponseEntity<>("User added succesfully", HttpStatus.OK);
        }catch(CashUserNotFoundException ex){
            return new ResponseEntity<>("Couldn't add user to account.", HttpStatus.BAD_REQUEST);

        }
    }

    @GetMapping("/members/{accountId}")
    public void getMembersForAccount(@PathVariable Long accountId){
        
    }

    @GetMapping("/{userID}")
    public List<AccountDTO> findAllUserAccounts(@PathVariable long userID){
       return accountsService.getAllAccountsOwnedByUser(userID);
    }

    @DeleteMapping("/delete/{accountID}")
    public ResponseEntity<Object> deleteAccount(@PathVariable Long accountID) {
        try {
            accountsService.removeAccount(accountID);
            return new ResponseEntity<>("Account deleted successfully", HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>("No account found.", HttpStatus.BAD_REQUEST);
        }
    }

    private EntityModel<AccountDTO> toModel(AccountDTO accountDTO, Long userId) {
        AccountDTO accountModel = new AccountDTO(accountDTO.getId(), accountDTO.getName(), userId);
        EntityModel<AccountDTO> model = EntityModel.of(accountModel);

        model.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AccountController.class).findAll()).withRel("all-accounts"));
        model.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AccountController.class).createAccount(null, null)).withRel("create-account"));
        model.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AccountController.class).deleteAccount(accountDTO.getId())).withRel("delete-account"));

        return model;
    }
}
