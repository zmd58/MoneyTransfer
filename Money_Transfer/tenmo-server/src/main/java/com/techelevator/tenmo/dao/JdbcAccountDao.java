package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * JdbcAccountDao used to access account table in database.
 */
@Component
public class JdbcAccountDao implements AccountDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * getAllAccounts retrieves all accounts in database.
     * @return List<Account>
     */
    @Override
    public List<Account> getAllAccounts() {
        List<Account> accounts = null;

        String sql = "SELECT account_id, user_id, balance FROM account";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            accounts.add(mapRowToAccount(results));
        }

        return accounts;
    }

    /**
     * updateAccount is used to change account balance of provided account
     * in the database.
     * @param account
     * @return
     */
    public boolean updateAccount(Account account) {
        String sql = "UPDATE account SET balance = ? WHERE user_id = ?;";
        int numberOfRows = jdbcTemplate.update(sql, account.getBalance(),account.getUserId());
        return numberOfRows == 1;
    }

    /**
     * getAccountByUserId is used to retrieve the account information from
     * the account table in the database.
     * @param userId
     * @return Account
     */
    @Override
    public Account getAccountByUserId(int userId) {
        Account account = null;

        String sql = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        if (results.next()) {
            account = mapRowToAccount(results);
        }

        return account;
    }

    /**
     * getAccountbyAccountId retrieves the account information for user with
     * the given accountId from the database.
     * @param accountId
     * @return Account
     */
    @Override
    public Account getAccountByAccountId(int accountId) {
        Account account = null;

        String sql = "SELECT account_id, user_id, balance FROM account WHERE account_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        if (results.next()) {
            account = mapRowToAccount(results);
        }

        return account;
    }

    /**
     * mapRowToAccount is used to deserialize the account object taken
     * from the account table in the database.
     * @param results
     * @return Account
     */
    private Account mapRowToAccount(SqlRowSet results) {
        Account account = new Account();
        account.setAccountId(results.getInt("account_id"));
        account.setUserId(results.getInt("user_id"));
        account.setBalance(results.getBigDecimal("balance"));
        return account;
    }


}
