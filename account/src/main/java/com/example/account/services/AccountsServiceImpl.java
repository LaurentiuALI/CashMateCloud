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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    public void addAccountMember(long accountID, Long userID) {
        log.info("Adding member to account: accountID={}, userId={}", accountID, userID);
        CashUserDTO user = cashUserServiceProxy.findById(userID);

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
    public List<AccountDTO> getAllAccountsOwnedAndParticipantByUser(long userID) {
        return List.of();
    }

    @Override
    public Page<AccountDTO> getAllAccountsOwnedAndParticipantByUser(long userID, int page, int size) {
        return null;
    }

    @Override
    public CashUserDTO getAccountOwner(AccountDTO account) {
        return null;
    }

    @Override
    public AccountDTO createAccount(AccountDTO accountDTO, Long userID){
        log.info("Creating account: {}", accountDTO);
        accountDTO.setUser_id(userID);
        Account account = modelMapper.map(accountDTO, Account.class);
        accountRepository.save(account);
        return accountDTO;
    }

    @Override
    public List<AccountDTO> getAll(){
        log.info("Retrieving all accounts..");

        List<Account> accounts = new LinkedList<>();
        accountRepository.findAll(Sort.by("id")).iterator().forEachRemaining(accounts::add);

        return accounts.stream().map(account -> modelMapper.map(account, AccountDTO.class)).collect(Collectors.toList());

    }

    @Override
    public List<CashUserDTO> getAccountMembers(long accountID) {

        List<CashUserDTO> users;

        users = userAccountRepository.findUserIDByAccountId(accountID).stream().map(
                cashUserServiceProxy::findById
        ).toList();

        return users;
    }

    @Override
    public AccountDTO getById(long accountID) {
        return null;
    }

    @Override
    public AccountDTO updateAccount(AccountDTO accountDTO) {
        return null;
    }

    @Override
    public String removeAccountMember(long accountID, long ownerID, long userID) {
        return "";
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
