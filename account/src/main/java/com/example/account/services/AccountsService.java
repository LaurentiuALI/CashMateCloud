package com.example.account.services;

import com.example.account.dtos.AccountDTO;
import com.example.account.dtos.CashUserDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface AccountsService {
    AccountDTO createAccount(AccountDTO accountDTO, Long userID);
    AccountDTO getById(long accountID);
    List<AccountDTO> getAll();
    AccountDTO updateAccount(long accountID, String name);
    void removeAccount(long accountID);
    CashUserDTO getAccountOwner(long account);

    void addAccountMember(long accountID, Long userID);

    List<AccountDTO> getAllAccountsOwnedByUser(long userID);
    List<CashUserDTO> getAllAccountMembers(long accountID);
    List<AccountDTO> getAllAccountsOwnedAndParticipantByUser(long userID);
    Page<AccountDTO> getAllAccountsOwnedAndParticipantByUser(long userID, int page, int size);

    List<CashUserDTO> getAccountMembers(long accountID);

    String removeAccountMember(long accountID, long userID);

}
