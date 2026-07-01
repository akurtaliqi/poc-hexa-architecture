package com.aku.domain.port.out;

import com.aku.domain.model.Card;

public interface CardRepository {
    Card findById(long cardId);
    void save(Card card);
}