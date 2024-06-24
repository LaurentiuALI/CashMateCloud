package com.example.account.services;

import com.example.account.dtos.AccountDTO;
import com.example.account.dtos.CashUserDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface AccountsService {
    AccountDTO createAccount(AccountDTO accountDTO, Long userID);
    List<AccountDTO> getAll();


    void addAccountMember(long accountID, Long userID);



    List<AccountDTO> getAllAccountsOwnedByUser(long userID);
    List<AccountDTO> getAllAccountsOwnedAndParticipantByUser(long userID);
    Page<AccountDTO> getAllAccountsOwnedAndParticipantByUser(long userID, int page, int size);
    CashUserDTO getAccountOwner(AccountDTO account);


    List<CashUserDTO> getAccountMembers(long accountID);
    AccountDTO getById(long accountID);

    AccountDTO updateAccount(AccountDTO accountDTO);

    String removeAccountMember(long accountID, long ownerID, long userID);
    void removeAccount(long accountID);

}
