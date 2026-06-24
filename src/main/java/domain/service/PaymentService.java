package domain.service;

import domain.model.Account;
import domain.model.CardPayment;
import domain.model.Payment;
import domain.model.PaymentStatus;
import domain.port.in.AuthorizePaymentUseCase;
import domain.port.out.AccountRepository;
import domain.port.out.PaymentRepository;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentService extends AuthorizePaymentUseCase {
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final AtomicLong paymentIdSequence = new AtomicLong(System.currentTimeMillis());

    public PaymentService(AccountRepository accountRepository, PaymentRepository paymentRepository) {
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentStatus authorizePayment(long accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }

        Account account = accountRepository.findById(accountId);
        Payment payment = createPayment(accountId, amount);

        if (account.getBalance().compareTo(amount) >= 0) {
            account.debit(amount);
            accountRepository.save(account);
            payment.accept();
        } else {
            payment.refuse();
        }

        savePayment(payment);
        return payment.getPaymentStatus();
    }

    public Payment createPayment(long accountId, BigDecimal amount) {
        long id = paymentIdSequence.incrementAndGet();
        return new CardPayment(id, accountId, amount);

    }

    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }
}
