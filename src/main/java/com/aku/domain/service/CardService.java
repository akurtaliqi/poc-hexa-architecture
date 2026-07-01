package com.aku.domain.service;

import com.aku.domain.model.Card;
import com.aku.domain.port.in.BlockCardUseCase;
import com.aku.domain.port.out.CardRepository;

public class CardService extends BlockCardUseCase {
    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public void blockCard(long cardId) {
        Card card = cardRepository.findById(cardId);
        card.block();
        cardRepository.save(card);
    }
}