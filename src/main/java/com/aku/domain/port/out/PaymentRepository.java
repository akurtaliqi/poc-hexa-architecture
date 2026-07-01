package com.aku.domain.port.out;

import com.aku.domain.model.Payment;

public interface PaymentRepository {
    void save(Payment payment);
}
