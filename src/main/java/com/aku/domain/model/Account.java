package com.aku.domain.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Account {

    private final long accountId;
    private BigDecimal availableAmount;
    private final Card card;

    public Account(long accountId, BigDecimal availableAmount, Card card) {
        if (availableAmount == null || availableAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
        this.accountId = accountId;
        this.availableAmount = availableAmount;
        this.card = card;
    }

    public BigDecimal getBalance() {
        return availableAmount;
    }

    public boolean hasSufficientBalance(BigDecimal amount) {
        return availableAmount.compareTo(amount) >= 0;
    }

    public void debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
        if (availableAmount.compareTo(amount) >= 0) {
            availableAmount = availableAmount.subtract(amount);
        } else {
            throw new IllegalArgumentException("Insufficient funds");
        }
    }
}