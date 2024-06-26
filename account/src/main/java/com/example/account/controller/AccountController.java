package com.example.account.controller;

import com.example.account.dtos.AccountDTO;
import com.example.account.dtos.CashUserDTO;
import com.example.account.exceptions.CashUserNotFoundException;
import com.example.account.exceptions.ResourceNotFoundException;
import com.example.account.services.AccountsService;
import com.example.account.services.CashUserServiceProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<EntityModel<AccountDTO>>> findAll() {
        log.info("Retrieving all accounts");
        List<EntityModel<AccountDTO>> accounts = accountsService.getAll().stream()
                .map(account -> toModel(account, account.getUser_id()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/owner/{accountId}")
    public ResponseEntity<Object> findAccountOwner(@RequestHeader("cashmate-id")
                                                       String correlationId, @PathVariable long accountId) {
        log.info("Retrieving owner for account ID: {}", accountId);
        try {
            return new ResponseEntity<>(accountsService.getAccountOwner(correlationId, accountId), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            log.error("Account owner not found: {}", ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create/{userID}")
    public ResponseEntity<Object> createAccount(@RequestHeader("cashmate-id")
                                                    String correlationId, @RequestBody AccountDTO account, @PathVariable Long userID) {
        log.info("Creating account for user ID: {}", userID);
        try {
            CashUserDTO user = cashUserServiceProxy.findById(correlationId, userID);
            if (user == null) {
                log.error("User not found for ID: {}", userID);
                return new ResponseEntity<>("No user found.", HttpStatus.BAD_REQUEST);
            } else {
                AccountDTO createdAccount = accountsService.createAccount(account, userID);
                EntityModel<AccountDTO> model = toModel(createdAccount, userID);
                return new ResponseEntity<>(model, HttpStatus.OK);
            }
        } catch (Exception ex) {
            log.error("Error creating account: {}", ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{accountID}")
    public ResponseEntity<Object> findAccount(@PathVariable long accountID) {
        log.info("Retrieving account ID: {}", accountID);
        try {
            AccountDTO account = accountsService.getById(accountID);
            EntityModel<AccountDTO> model = toModel(account, account.getUser_id());
            return new ResponseEntity<>(model, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            log.error("Account not found: {}", ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{accountID}")
    public ResponseEntity<Object> updateAccountName(@PathVariable long accountID, @RequestBody AccountDTO newAccount) {
        log.info("Updating account name for ID: {}", accountID);
        try {
            AccountDTO account = accountsService.updateAccount(accountID, newAccount.getName());
            EntityModel<AccountDTO> model = toModel(account, accountID);
            return new ResponseEntity<>(model, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            log.error("Account not found: {}", ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{accountID}")
    public ResponseEntity<Object> deleteAccount(@PathVariable Long accountID) {
        log.info("Deleting account ID: {}", accountID);
        try {
            accountsService.removeAccount(accountID);
            return new ResponseEntity<>("Account deleted successfully.", HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            log.error("Account not found: {}", ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/addMember/{accountId}/{userID}")
    public ResponseEntity<Object> addMemberToAccount(@RequestHeader("cashmate-id")
                                                         String correlationId, @PathVariable Long accountId, @PathVariable Long userID) {
        log.info("Adding member to account ID: {}", accountId);
        try {
            accountsService.addAccountMember(correlationId, accountId, userID);
            AccountDTO account = accountsService.getById(accountId);
            EntityModel<AccountDTO> model = toModel(account, accountId);
            return new ResponseEntity<>(model, HttpStatus.OK);
        } catch (CashUserNotFoundException ex) {
            log.error("User not found: {}", ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (ResourceNotFoundException ex) {
            log.error("Account not found: {}", ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/members/{accountId}")
    public ResponseEntity<Object> getMembersForAccount(@RequestHeader("cashmate-id")
                                                           String correlationId, @PathVariable Long accountId) {
        log.info("Retrieving members for account ID: {}", accountId);
        try {
            List<String> response = accountsService.getAccountMembers(correlationId, accountId).stream()
                    .map(CashUserDTO::getName)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            log.error("Account not found: {}", ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/members/{accountId}/{userId}")
    public ResponseEntity<Object> removeMembersFromAccount(@PathVariable Long accountId, @PathVariable Long userId) {
        log.info("Removing member from account ID: {}", accountId);
        try {
            String response = accountsService.removeAccountMember(accountId, userId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            log.error("Account or user not found: {}", ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private EntityModel<AccountDTO> toModel(AccountDTO accountDTO, Long userId) {
        EntityModel<AccountDTO> model = EntityModel.of(accountDTO);

        model.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AccountController.class).findAccount(accountDTO.getId())).withSelfRel());
        model.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AccountController.class).findAll()).withRel("all-accounts"));
        model.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AccountController.class).createAccount(null, null, userId)).withRel("create-account"));
        model.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AccountController.class).deleteAccount(accountDTO.getId())).withRel("delete-account"));
        model.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AccountController.class).addMemberToAccount(null, accountDTO.getId(), userId)).withRel("add-member"));
        model.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AccountController.class).findAccountOwner(null, accountDTO.getId())).withRel("account-owner"));
        model.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AccountController.class).getMembersForAccount(null, accountDTO.getId())).withRel("account-members"));
        model.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AccountController.class).updateAccountName(accountDTO.getId(), accountDTO)).withRel("update-account-name"));
        model.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AccountController.class).removeMembersFromAccount(accountDTO.getId(), userId)).withRel("remove-member"));

        return model;
    }

}
