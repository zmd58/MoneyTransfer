package com.techelevator.tenmo.model;

import java.math.BigDecimal;

/**
 * model dto to send transfer id, accountTo, and amount of transfer.
 */
public class TransferPendingDto {
    private int id;

    private String accountTo;

    private BigDecimal amount;

    public TransferPendingDto(int id, String accountTo, BigDecimal amount) {
        this.id = id;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public TransferPendingDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(String accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
