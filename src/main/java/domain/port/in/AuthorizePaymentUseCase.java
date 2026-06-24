package domain.port.in;


import domain.model.PaymentStatus;

import java.math.BigDecimal;

public abstract class AuthorizePaymentUseCase {

    public abstract PaymentStatus authorizePayment(long accountId, BigDecimal amount);
}