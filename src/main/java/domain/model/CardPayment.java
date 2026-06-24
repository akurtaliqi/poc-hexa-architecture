package domain.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CardPayment extends Payment {
    public CardPayment(long id, long accountId, BigDecimal amount) {
        super(id, accountId, amount);
    }
}
