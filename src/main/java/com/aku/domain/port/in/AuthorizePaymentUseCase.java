package com.aku.domain.port.in;


import com.aku.domain.model.PaymentStatus;

import java.math.BigDecimal;

public abstract class AuthorizePaymentUseCase {

    public abstract PaymentStatus authorizePayment(long accountId, BigDecimal amount);
}