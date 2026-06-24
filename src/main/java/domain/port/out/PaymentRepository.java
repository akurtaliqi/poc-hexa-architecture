package domain.port.out;

import domain.model.Payment;

public interface PaymentRepository {
    void save(Payment payment);
}
