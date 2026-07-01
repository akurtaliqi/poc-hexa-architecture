package com.aku.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class Payment {
    private final long id;
    private final long accountId;
    private final BigDecimal amount;
    private PaymentStatus paymentStatus;
    private final LocalDateTime paymentDate;

    public Payment(long id, long accountId, BigDecimal amount, LocalDateTime paymentDate) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.paymentStatus = PaymentStatus.PENDING;
        this.paymentDate = paymentDate;
    }

    public void accept() {
        this.paymentStatus = PaymentStatus.ACCEPTED;
    }

    public void refuse() {
        this.paymentStatus = PaymentStatus.REFUSED;
    }
}
