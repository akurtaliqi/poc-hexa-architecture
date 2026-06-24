package domain.port.out;

import domain.model.Account;

public interface AccountRepository {
    Account findById(long accountId);
    void save(Account account);
}
