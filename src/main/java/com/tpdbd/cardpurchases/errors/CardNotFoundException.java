package com.tpdbd.cardpurchases.errors;

public class CardNotFoundException extends NotFoundException {
    public CardNotFoundException(long id) {
        super("Card not found, id = %d", id);
    }
}
