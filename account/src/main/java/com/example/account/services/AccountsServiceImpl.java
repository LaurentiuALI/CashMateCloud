package com.example.account.services;

import com.example.account.dtos.AccountDTO;
import com.example.account.dtos.CashUserDTO;
import com.example.account.exceptions.CashUserNotFoundException;
import com.example.account.exceptions.ResourceNotFoundException;
import com.example.account.model.Account;
import com.example.account.model.UserAccount;
import com.example.account.model.UserAccountId;
import com.example.account.repositories.AccountRepository;
import com.example.account.repositories.UserAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountsServiceImpl implements AccountsService{

    private final AccountRepository accountRepository;
    private final CashUserServiceProxy cashUserServiceProxy;
    private final UserAccountRepository userAccountRepository;
    private final ModelMapper modelMapper;

    public AccountsServiceImpl(AccountRepository accountRepository, CashUserServiceProxy cashUserServiceProxy, UserAccountRepository userAccountRepository,
                               ModelMapper modelMapper) {
        this.accountRepository = accountRepository;
        this.cashUserServiceProxy = cashUserServiceProxy;
        this.userAccountRepository = userAccountRepository;
        this.modelMapper = modelMapper;
        log.info("AccountsServiceImpl instantiated.");
    }

    @Override
    public void addAccountMember(@RequestHeader("cashmate-id")
                                     String correlationId, long accountID, Long userID) {
        log.info("Adding member to account: accountID={}, userId={}", accountID, userID);
        CashUserDTO user = cashUserServiceProxy.findById(correlationId, userID);

        if(user == null){
            throw new CashUserNotFoundException("Couldn't find user to associate!");
        }
        UserAccountId userAccountId = new UserAccountId(userID, accountID);

        Optional<Account> accountOpt = accountRepository.findById(accountID);
        if(accountOpt.isPresent()){
            UserAccount userAccount = new UserAccount(userAccountId, accountOpt.get());
            userAccountRepository.save(userAccount);
            log.info("User {} added to account successfully", userID);
        } else {
            throw new CashUserNotFoundException("Couldn't find account to associate!");
        }
    }

    @Override
    public List<AccountDTO> getAllAccountsOwnedByUser(long userID) {

        List<Account> accounts = new LinkedList<>();
        accountRepository.findAll(Sort.by("id")).iterator().forEachRemaining(accounts::add);
        accounts = accounts.stream().filter(account -> account.getUser_id() == userID).collect(Collectors.toList());

        return accounts.stream().map(account -> modelMapper.map(account, AccountDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<CashUserDTO> getAllAccountMembers(@RequestHeader("cashmate-id")
                                                      String correlationId, long accountID) {

        List<Long> userIDs = userAccountRepository.findUserIDByAccountId(accountID);

        return userIDs.stream().map(userId -> cashUserServiceProxy.findById(correlationId, userId)).collect(Collectors.toList());

    }

    @Override
    public List<AccountDTO> getAllAccountsOwnedAndParticipantByUser(long userID) {
        return List.of();
    }

    @Override
    public Page<AccountDTO> getAllAccountsOwnedAndParticipantByUser(long userID, int page, int size) {
        return null;
    }

    @Override
    public CashUserDTO getAccountOwner(@RequestHeader("cashmate-id")
                                           String correlationId, long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if(account.isEmpty()){
            throw new ResourceNotFoundException("Account with id " + accountId + " not found.");
        }
        return cashUserServiceProxy.findById(correlationId, account.get().getUser_id());
    }

    @Override
    public AccountDTO createAccount(AccountDTO accountDTO, Long userID){
        log.info("Creating account: {}", accountDTO);
        accountDTO.setUser_id(userID);
        Account account = modelMapper.map(accountDTO, Account.class);
        accountRepository.save(account);


        return modelMapper.map(account, AccountDTO.class);
    }

    @Override
    public List<AccountDTO> getAll(){
        log.info("Retrieving all accounts..");

        List<Account> accounts = new LinkedList<>();
        accountRepository.findAll(Sort.by("id")).iterator().forEachRemaining(accounts::add);

        return accounts.stream().map(account -> modelMapper.map(account, AccountDTO.class)).collect(Collectors.toList());

    }

    @Override
    public List<CashUserDTO> getAccountMembers(@RequestHeader("cashmate-id")
                                                   String correlationId, long accountID) {
        Optional<Account> account = accountRepository.findById(accountID);

        if(account.isEmpty()){
            throw new ResourceNotFoundException("Account with id " + accountID + " not found.");
        }

        List<CashUserDTO> users;
        users = userAccountRepository.findUserIDByAccountId(accountID).stream().map(
                userId -> cashUserServiceProxy.findById(correlationId, userId)
        ).toList();

        return users;
    }

    @Override
    public AccountDTO getById(long accountID) {

        Optional<Account> account = accountRepository.findById(accountID);

        if(account.isEmpty()){
            throw new ResourceNotFoundException("Account with id " + accountID + " not found.");
        }
        return modelMapper.map(account.get(), AccountDTO.class);
    }

    @Override
    public AccountDTO updateAccount(long accountID, String name){
        log.info("Updating account with id: {}", accountID);
        Optional<Account> accountOpt = accountRepository.findById(accountID);

        if(accountOpt.isPresent()){
            Account account = accountOpt.get();
            account.setName(name);
            Account updatedAccount = accountRepository.save(account);
            return modelMapper.map(updatedAccount, AccountDTO.class);
        } else {
            log.error("Cannot update account. Account with ID {} was not found", accountID);
            throw new ResourceNotFoundException("Account with ID " + accountID + " was not found");
        }
    }

    @Override
    public String removeAccountMember(long accountID, long userID) {
        Optional<Account> account = accountRepository.findById(accountID);

        if(account.isEmpty()){
            throw new ResourceNotFoundException("Account with id " + accountID + " not found.");
        }

        if(userAccountRepository.findUserIDByAccountId(accountID).contains(userID)){
            userAccountRepository.deleteByAccountIdAndUserId(accountID, userID);
        } else{
            throw new ResourceNotFoundException("Account with id " + accountID + " don't have user with id " + userID + " among members.");
        }

        return "Member removed successfully.";
    }

    @Override
    public void removeAccount(long accountID) {

        Optional<Account> account = accountRepository.findById(accountID);

        if(account.isEmpty()){
            throw new ResourceNotFoundException("Account with ID " + accountID + " was not found");
        } else{
            accountRepository.deleteById(accountID);
        }
    }

}
