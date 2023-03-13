package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.util.List;

public interface AccountDao {

    List<Account> getAllAccounts();
    
    Account getAccountByUserId(int userId);

    boolean updateAccount(Account account);

    Account getAccountByAccountId(int accountId);
}
