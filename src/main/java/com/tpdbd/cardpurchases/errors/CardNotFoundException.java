package com.tpdbd.cardpurchases.errors;

public class CardNotFoundException extends NotFoundException {
    public CardNotFoundException(String id) {
        super("Card not found, id = %s", id);
    }
}
