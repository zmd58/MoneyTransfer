package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.model.Account;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

public class JdbcAccountDaoTest extends BaseDaoTests {
    private static final Account ACCOUNT_1 = new Account(1001, 1001);
    private static final  Account ACCOUNT_2 = new Account(1002, 1002);
    private static final  Account ACCOUNT_3 = new Account(1003, 1003);
    private static final Account ACCOUNT_NOUSER = new Account(1004, 1004);
    private JdbcAccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }
    @Test
    public void testUpdateAccount_Updates_Correctly() {
        Account account = ACCOUNT_1;
        account.setBalance(BigDecimal.valueOf(1000));
        Assert.assertEquals(BigDecimal.valueOf(1000), account.getBalance());
        account.setBalance(BigDecimal.valueOf(2000));
        sut.updateAccount(account);
        Assert.assertEquals(BigDecimal.valueOf(2000), account.getBalance());

    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testUpdateAccount_UpdateBalanceWithNull_Returns_NullPointer() {
        ACCOUNT_1.setBalance(BigDecimal.valueOf(1000));
        ACCOUNT_1.setBalance(null);
        sut.updateAccount(ACCOUNT_1);
    }
}