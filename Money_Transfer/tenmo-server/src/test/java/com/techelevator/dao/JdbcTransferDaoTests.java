package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;


public class JdbcTransferDaoTests extends BaseDaoTests {
    protected static final User USER_1 = new User(1001, "user1", "user1", "USER");
    protected static final User USER_2 = new User(1002, "user2", "user2", "USER");
    private static final User USER_3 = new User(1003, "user3", "user3", "USER");
    private static final  Account ACCOUNT_1 = new Account(1001, 1001);
    private static final  Account ACCOUNT_2 = new Account(1002, 1002);
    private static final  Account ACCOUNT_3 = new Account(1003, 1003);
    private static final Account ACCOUNT_NO_USER = new Account(1004, 1004);
    private static final Transfer TRANSFER_1 = new Transfer( 1, 1, 1001, 1002, BigDecimal.valueOf(10).setScale(2));

    private JdbcTransferDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransferDao(jdbcTemplate);
    }


    @Test(expected = DataIntegrityViolationException.class)
    public void createTransferRequest_With_Unregistered_User_Returns_DataIntegrityViolation(){
        Transfer transfer = new Transfer(1,1,
                ACCOUNT_NO_USER.getAccountId(),ACCOUNT_2.getAccountId(),
                BigDecimal.TEN);
        Transfer transferRequestDto = sut.createTransferRequest(transfer);

    }

    @Test(expected = DataIntegrityViolationException.class)
    public void createTransferRequest_With_NullValue_Returns_DataIntegrityViolation() {
        Transfer transfer = new Transfer(1,1,
                ACCOUNT_3.getAccountId(), ACCOUNT_2.getAccountId(),
                null);
        Transfer transferRequestDto = sut.createTransferRequest(transfer);
    }




}