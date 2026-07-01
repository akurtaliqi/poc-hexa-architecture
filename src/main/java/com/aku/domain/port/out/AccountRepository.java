package com.aku.domain.port.out;

import com.aku.domain.model.Account;

public interface AccountRepository {
    Account findById(long accountId);
    void save(Account account);
}
