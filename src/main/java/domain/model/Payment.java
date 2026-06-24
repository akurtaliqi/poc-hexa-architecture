package domain.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public abstract class Payment {
    private final long id;
    private final long accountId;
    private final BigDecimal amount;
    private final PaymentStatus paymentStatus;

    public Payment(long id, long accountId, BigDecimal amount) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.paymentStatus = PaymentStatus.PENDING;
    }
}
