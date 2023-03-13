package com.techelevator.tenmo.model;

import java.math.BigDecimal;

/**
 * used to send transfer information to client side requests.
 */
public class TransferDto {
    private int id;
    private String transferType;
    private String transferStatus;
    private String accountToUsername;
    private String accountFromUsername;
    private int accountFromId;

    private int accountToId;

    private BigDecimal amount;

    //region Constructors, Setter, & Getters
    public TransferDto() {
    }

    public TransferDto(int id, String accountToUsername, BigDecimal amount) {
        this.id = id;
        this.accountToUsername = accountToUsername;
        this.amount = amount;
    }

    public TransferDto(int id, String transferType, String transferStatus, int accountFromId, int accountToId, BigDecimal amount) {
        this.id = id;
        this.transferType = transferType;
        this.transferStatus = transferStatus;
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amount = amount;
    }

    public TransferDto(int id, String transferType, String transferStatus, String accountFromUsername, String accountToUsername, BigDecimal amount) {
        this.id = id;
        this.transferType = transferType;
        this.transferStatus = transferStatus;
        this.accountFromUsername = accountFromUsername;
        this.accountToUsername = accountToUsername;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountToUsername() {
        return accountToUsername;
    }

    public void setAccountToUsername(String accountToUsername) {
        this.accountToUsername = accountToUsername;
    }

    public String getAccountFromUsername() {
        return accountFromUsername;
    }

    public void setAccountFromUsername(String accountFromUsername) {
        this.accountFromUsername = accountFromUsername;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    public int getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(int accountFromId) {
        this.accountFromId = accountFromId;
    }

    public int getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(int accountToId) {
        this.accountToId = accountToId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    //endregion
}
