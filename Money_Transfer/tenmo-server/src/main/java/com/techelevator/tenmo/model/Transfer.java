package com.techelevator.tenmo.model;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 *
 */
public class Transfer {
    private int id;
    @Positive
    private int transferType;
    @Positive
    private int transferStatus;
    @Positive
    private int accountFrom;
    @Positive
    private int accountTo;
    @Positive(message = "Amount entered must be positive")
    private BigDecimal amount;

//region Constructors, Getters, & Setters
    public Transfer(int transferType, int transferStatus, int accountFrom, int accountTo, BigDecimal amount) {
        this.transferType = transferType;
        this.transferStatus = transferStatus;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public Transfer() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransferType() {
        return transferType;
    }

    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }

    public int getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(int transferStatus) {
        this.transferStatus = transferStatus;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


    @Override
    public String toString() {
        return " ----- Transfer " +
                " Id - " + id +
                " | Transfer Type - " + transferType +
                " | Account From - " + accountFrom +
                " | Account To - " + accountTo +
                " | Amount - " + amount +
                " | Transfer Status - " + transferStatus +
                " -----";
    }
    //endregion
}
