package com.tpdbd.cardpurchases.errors;

public class CardHolderNotFoundException extends NotFoundException {
    public CardHolderNotFoundException(String id) {
        super("Card holder not found, id = %s", id);
    }
}
